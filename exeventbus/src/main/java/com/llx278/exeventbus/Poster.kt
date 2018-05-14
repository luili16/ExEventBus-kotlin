package com.llx278.exeventbus

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.llx278.exeventbus.remote.Address
import com.llx278.exeventbus.remote.IReceiver
import com.llx278.exeventbus.remote.Transport
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val TAG = "ExEventBus"
/**
 * 此key代表了每一个消息的类型
 */
private const val KEY_TYPE = "type"

/**
 * 此value代表向其他进程查询已经注册的订阅事件
 */
private const val TYPE_VALUE_OF_QUERY = "query_event"

/**
 * 此value代表查询已经注册的订阅事件的结果
 */
private const val TYPE_VALUE_OF_QUERY_RESULT = "query_event_result"

/**
 * 向其他进程的总线上发布一个订阅事件
 */
private const val TYPE_VALUE_OF_PUBLISH = "publish_event"

/**
 * 向其他进程的总线上发布此订阅事件执行后的返回值
 */
private const val TYPE_VALUE_OF_PUBLISH_RETURN_VALUE = "publish_event_value"

/**
 * 此key封装了查询订阅事件的列表
 */
private const val KEY_QUERY_LIST = "query_list"

/**
 * 此key封装了发布事件的参数
 */
private const val KEY_EVENT_OBJ = "publish_event_obj"

/**
 * 此key封装了发布事件的tag
 */
private const val KEY_TAG = "publish_tag"

/**
 * 此key分装了发布事件的返回值类型
 */
private const val KEY_RETURN_CLASS_NAME = "publish_return_class_name"

/**
 * 此key封装了发布事件执行完毕后的返回值
 */
private const val KEY_RETURN_VALUE = "return_value"

/**
 * 此key封装了每个消息唯一标志
 */
private const val KEY_ID = "router_id"

/**
 * Poster封装了如何将事件投递到其他的进程去
 */
class Poster(context: Context, private val eventBus: EventBus) : IReceiver {

    private val transport: Transport = Transport(context)
    /**
     * 保存了对接收到的消息进行处理的类的列表
     */
    private val messageObserverList = ArrayList<MessageObserver>()

    /**
     * 保存了等待其他进程执行结束返回的结果的列表
     */
    private val waitingExecuteReturnValue = ConcurrentHashMap<String, PublishHandler>()

    /**
     * 保存了每个消息所对一个的那一时刻的事件列表
     *
     * 我没有考虑缓存所有进程的事件列表的原因是每个进程随时都有可能被杀死，这样的缓存列表很难维护，到不如
     * 每次都做一次查询，这样虽然效率会低点，但能避免很多bug。
     */
    private val subscribeEventListSnapshot = ConcurrentHashMap<String, EventListHolder>()

    init {
        transport.addReceiver(this)

        messageObserverList.add(ResultHandler())
        messageObserverList.add(ExecuteHandler())
        messageObserverList.add(QueryHandler())
        messageObserverList.add(QueryResultHandler())
    }

    override fun onMessageReceive(where: String?, message: Bundle?) {
        if (where != null && message != null) {
            messageObserverList.forEach {
                if (it.handleMessage(where, message)) {
                    return
                }
            }
        }
    }

    /**
     * 将此事件发送到其他的进程中执行
     * 注意，当一个事件被发布到多个进程中执行的时候，如果returnClassName是void.class.getName(),那么
     * 则只会只将此事件发送到其他的进程执行，直接返回。否则会等待，直到返回当前的执行后的结果。
     *
     * @return 返回执行的结果，如果方法的返回值是void，则默认返回null，如果不是，则返回执行后的结果
     * @throws TimeoutException 超时
     */
    fun post(eventObj: Any?, tag: String, returnType: String, timeout: Long): Any? {
        val aliveClients = transport.aliveClient
        if (aliveClients.isEmpty()) {
            Log.e(TAG, "no available address")
            return null
        }

        val signal = CountDownLatch(aliveClients.size)
        val id = UUID.randomUUID().toString()
        subscribeEventListSnapshot[id] = EventListHolder(signal)

        // 发送消息
        val message = Bundle()
        message.putString(KEY_ID, id)
        message.putString(KEY_TYPE, TYPE_VALUE_OF_QUERY)
        val broadcastAddress = Address.toBroadcastAddress()
        transport.send(broadcastAddress.toString(), message)

        try {

            if (!signal.await(timeout, TimeUnit.MILLISECONDS)) {
                subscribeEventListSnapshot.remove(id)
                throw TimeoutException("query value timeout")
            }
        } catch (e: InterruptedException) {
            Log.e(TAG, "unExcepted interrupt when query value")
        }
        val paramType: String? = if (eventObj == null) {
            Unit::class.qualifiedName
        } else {
            eventObj::class.qualifiedName
        }

        if (paramType == null) {
            subscribeEventListSnapshot.remove(id)
            Log.e(TAG, "empty qualified name in $eventObj")
            return null
        }
        val event = Event(paramType = paramType, tag = tag, returnType = returnType, remote = true)
        val eventListHolder = subscribeEventListSnapshot[id]
        if (eventListHolder == null) {
            subscribeEventListSnapshot.remove(id)
            Log.e(TAG, "empty eventListHolder")
            return null
        }
        val eventsMap = eventListHolder.eventsMap
        val addressList = getAddressOf(event, eventsMap = eventsMap)
        if (addressList.isEmpty()) {
            Log.e(TAG, "no available event list")
            subscribeEventListSnapshot.remove(id)
            return null
        }
        subscribeEventListSnapshot.remove(id)

        return if (returnType == Unit::class.qualifiedName) {
            addressList.forEach {
                post(it, eventObj, tag, returnType, timeout)
            }
            null
        } else {
            val address = addressList[0]
            post(address, event, tag, returnType, timeout)
        }
    }

    private fun getAddressOf(event: Event, eventsMap: ConcurrentHashMap<String, ArrayList<Event>>): ArrayList<String> {
        val addressList = ArrayList<String>()
        eventsMap.forEach({
            val address = it.key
            val eventsList = it.value
            eventsList.forEach {
                if (it == event) {
                    addressList.add(address)
                }
            }
        })
        return addressList
    }

    /**
     * 将事件发送到指定的进程执行
     */
    private fun post(address: String, eventObj: Any?, tag: String, returnType: String, timeout: Long): Any? {
        val publishHandler = PublishHandler(address = address)
        return publishHandler.publishToRemote(eventObj, tag, returnType, timeout)
    }

    override fun asBinder(): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 发布订阅事件
     */
    private inner class PublishHandler(private val address: String) {

        /**
         * 这个事件的唯一标识
         */
        private val id: String = UUID.randomUUID().toString()

        lateinit var result: Any

        val doneSignal = CountDownLatch(1)

        fun publishToRemote(eventObj: Any?, tag: String, returnType: String, timeout: Long): Any? {

            // 封装消息
            val message = Bundle()
            message.putString(KEY_TYPE, TYPE_VALUE_OF_PUBLISH)
            message.putString(KEY_ID, id)// 其他类型需要缓存执行的事件，等待执行结果的返回
            // 直接发送，不需要等待返回值
            when {
                eventObj is Serializable -> message.putSerializable(KEY_EVENT_OBJ, eventObj)
                eventObj is Parcelable -> message.putParcelable(KEY_EVENT_OBJ, eventObj)
                eventObj != null -> throw  IllegalArgumentException("eventObj(${eventObj::class.qualifiedName} is " +
                        "not implement Serializable or Parcelable")
            }

            message.putString(KEY_TAG, tag)
            message.putString(KEY_RETURN_CLASS_NAME, returnType)

            if (returnType == Unit::class.qualifiedName) {
                // 直接发送，不需要等待返回值
                transport.send(address, message)
            } else {
                // 其他类型需要缓存执行的事件，等待执行结果的返回
                waitingExecuteReturnValue[id] = this
                transport.send(address, message)
                try {
                    if (!doneSignal.await(timeout, TimeUnit.MILLISECONDS)) {
                        throw TimeoutException("waiting result from remote process timeout")
                    }
                } catch (e: InterruptedException) {
                    Log.e(TAG, "UnExcepted interrupt when waiting result from remote process", e)
                } finally {
                    waitingExecuteReturnValue.remove(id)
                }
                return result
            }
            return null
        }
    }

    // --------------------- MessageObserver的不同实现 -------------------------
    /**
     * 用来处理查询注册列表的类
     */
    private inner class QueryHandler : MessageObserver {
        override fun handleMessage(where: String, message: Bundle): Boolean {
            val typeValue = message.getString(KEY_TYPE)
            if (TYPE_VALUE_OF_QUERY == typeValue) {
                val allEvents = eventBus.queryRemote()
                val id = message.getString(KEY_ID)
                val valueMessage = Bundle()
                valueMessage.putString(KEY_ID, id)
                valueMessage.putString(KEY_TYPE, TYPE_VALUE_OF_QUERY_RESULT)
                valueMessage.classLoader = javaClass.classLoader
                valueMessage.putParcelableArrayList(KEY_QUERY_LIST, allEvents)
                try {
                    transport.send(where, valueMessage)
                } catch (e: RemoteException) {
                    Log.e(TAG, "send query event list to ${Address.toAddress(where)} failed!", e)
                }
                return true
            }
            return false
        }
    }

    /**
     * 用来处理查询订阅注册列表的结果类
     */
    private inner class QueryResultHandler : MessageObserver {
        override fun handleMessage(where: String, message: Bundle): Boolean {
            val typeValue = message.getString(KEY_TYPE)
            if (TYPE_VALUE_OF_QUERY_RESULT == typeValue) {

                val queryEvents = message.getParcelableArrayList<Event>(KEY_QUERY_LIST)
                val id = message.getString(KEY_ID)
                val eventListHolder = subscribeEventListSnapshot[id]
                if (eventListHolder != null) {
                    eventListHolder.eventsMap[where] = queryEvents
                    eventListHolder.signal.countDown()
                }
                return true
            }
            return false
        }
    }

    /**
     * 执行发布事件
     */
    private inner class ExecuteHandler : MessageObserver {
        override fun handleMessage(where: String, message: Bundle): Boolean {
            val typeValue = message.getString(KEY_TYPE)
            if (TYPE_VALUE_OF_PUBLISH == typeValue) {
                // 执行一个发布的订阅事件
                val id = message.getString(KEY_ID)
                val eventObj = message.get(KEY_EVENT_OBJ)
                val tag = message.getString(KEY_TAG)
                val returnType = message.getString(KEY_RETURN_CLASS_NAME)
                val returnValue = eventBus.publish(eventObj, tag, returnType, true)
                // 只有返回值是非空的才会发送回去
                if (!TextUtils.isEmpty(returnType) && returnType != "kotlin.Unit") {
                    val valueMessage = Bundle()
                    valueMessage.putString(KEY_TYPE, TYPE_VALUE_OF_PUBLISH_RETURN_VALUE)
                    if (returnValue != null) {
                        when (returnValue) {
                            is Serializable -> valueMessage.putSerializable(KEY_RETURN_VALUE, returnValue)
                            is Parcelable -> valueMessage.putParcelable(KEY_RETURN_VALUE, returnValue)
                            else -> throw IllegalArgumentException("eventObj(" + returnValue::class.qualifiedName
                                    + ") is not implement Serializable or Parcelable")
                        }
                    }
                    valueMessage.putString(KEY_ID, id)
                    transport.send(where, valueMessage)
                }
                return true
            }
            return false
        }
    }

    /**
     * 等待返回结果
     */
    private inner class ResultHandler : MessageObserver {
        override fun handleMessage(where: String, message: Bundle): Boolean {
            val typeValue = message.getString(KEY_TYPE)
            if (TYPE_VALUE_OF_PUBLISH_RETURN_VALUE == typeValue) {
                // 接收到了一个订阅事件的执行结果
                val returnValue = message.get(KEY_RETURN_VALUE)
                val id = message.getString(KEY_ID)
                if (!TextUtils.isEmpty(id)) {
                    val waitingPublishHandler = waitingExecuteReturnValue[id]
                    if (waitingPublishHandler != null) {
                        waitingPublishHandler.result = returnValue
                        waitingPublishHandler.doneSignal.countDown()
                    }
                }
                return true
            }
            return false
        }
    }
}

private class EventListHolder(val signal: CountDownLatch) {
    val eventsMap: ConcurrentHashMap<String, ArrayList<Event>> = ConcurrentHashMap()
}

/**
 * 消息处理接口
 */
private interface MessageObserver {
    /**
     * 处理一个到达的消息
     * @param where 地址
     * @param message 消息
     * @return true 此条消息已经被处理 false 没有处理
     */
    fun handleMessage(where: String, message: Bundle): Boolean
}

class TimeoutException(message: String?) : Exception(message)

