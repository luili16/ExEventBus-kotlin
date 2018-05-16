package com.llx278.exeventbus

import android.util.Log
import com.llx278.exeventbus.execute.Executor
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.declaredFunctions

class EventBus {
    private val tag = "ExEventBus"
    internal val subscribedMap: MutableMap<Event, CopyOnWriteArrayList<Subscription>> = ConcurrentHashMap()

    /**
     * 订阅一个事件
     *
     * 注意： 同一个订阅事件不要重复注册，这样会导致一个订阅事件会被执行多次
     *
     * @throws IllegalStateException 当订阅方法的返回值不为空，并且type=Type.DEFAULT的时候会抛出此异常，因为
     * 当一个订阅方法有返回值的时候则需要将返回值返回去，这意味着需要阻塞当前的线程
     */
    fun register(subscriber: Any) {

        val kClass = subscriber::class
        val iterator = kClass.declaredFunctions.iterator()
        while (iterator.hasNext()) {
            val kFunction = iterator.next()
            val annotations = kFunction.annotations
            val subsAnnotation = annotations.find { it is Subscriber }
            // 找到了一个Subscriber
            if (subsAnnotation != null && subsAnnotation is Subscriber) {
                // 判断函数参数，只允许函数有一个或者零个参数
                val size = kFunction.parameters.size
                if (size >= 3) {
                    Log.e(tag, "$kFunction : index error,subScribe method only permit one or empty parameter")
                    continue
                }

                var paramType: String
                paramType = when (size) {
                    1 -> "kotlin.Unit"
                    2 -> kFunction.parameters[1].type.toString()
                    else -> throw RuntimeException("this should never happen!")
                }

                val returnType = kFunction.returnType.toString()

                if (returnType != "kotlin.Unit" && subsAnnotation.type != Type.BLOCK) {
                    throw IllegalStateException("illegal type and returnType in subscriber annotation," +
                            "you should let 'type = Type.BLOCK' when 'returnType != kotlin.Unit'")
                }

                val event = Event(paramType, subsAnnotation.tag, returnType, subsAnnotation.remote)
                // 找到此event对应的subscriber列表
                var subscriptionList: CopyOnWriteArrayList<Subscription>
                subscriptionList = if (!subscribedMap.containsKey(event)) {
                    CopyOnWriteArrayList()
                } else {
                    subscribedMap.getValue(event)
                }

                val type = subsAnnotation.type
                val weakSubscriber: WeakReference<Any> = WeakReference(subscriber)
                val subscription = Subscription(weakSubscriber, subsAnnotation.threadModel, type, kFunction)
                // 返回值不为空的情况下，只能保证只有一个方法被执行，因为这个方法执行完毕后的值是需要返回去的
                // 这里只保证subscriptionList里只有一个元素
                if ("kotlin.Unit" != returnType && subscriptionList.isEmpty()) {
                    subscriptionList.add(subscription)
                } else {
                    subscriptionList.add(subscription)
                }
                subscribedMap[event] = subscriptionList
            }
        }
    }

    fun unRegister(subscriber: Any) {
        val entries = subscribedMap.entries
        val iterator = entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val subscriptionList = entry.value
            subscriptionList.forEach {
                val cachedSubscriber = it.subscribeRef.get()
                if (cachedSubscriber == null || cachedSubscriber == subscriber) {
                    subscriptionList.remove(it)
                }
                if (subscriptionList.isEmpty()) {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * 向EventBus上发布一个事件，eventObj,tag,returnType和remote唯一标志了Event,所有匹配订阅事件的订阅方法
     * 都会被执行。
     *
     * 注意：如果订阅方法的返回值为空，那么就会返回null，自行忽略就好了
     */
    fun publish(eventObj: Any? = null,
                tag: String,
                returnType: String = "kotlin.Unit",
                remote: Boolean = false): Any? {

        val paramType = if (eventObj == null) {
            "kotlin.Unit"
        } else {
            eventObj::class.qualifiedName
                    ?: throw IllegalArgumentException("invalid event Obj : $eventObj")
        }
        val event = Event(paramType, tag, returnType, remote)
        val subscriptionList = subscribedMap[event]
        if (subscriptionList == null) {
            Log.e("ExEventBus", "EventBus : cannot find $event from subscribedMap")
            return null
        }
        subscriptionList.forEach {
            val executor = Executor.creator(it.threadModel)
            val subscribe = it.subscribeRef.get()
            if (subscribe != null) {
                if (it.type == Type.BLOCK) {
                    // 因为返回值只能有一个，所以默认只是第一个注册的有效
                    return executor.submit(it.kFunc, eventObj, subscribe)
                } else if (it.type == Type.DEFAULT) {
                    executor.execute(it.kFunc, eventObj, subscribe)
                }
            }
        }
        return null
    }

    /**
     * 返回可以远程发布的事件列表
     */
    fun queryRemote(): ArrayList<Event> {
        val eventList = ArrayList<Event>()
        subscribedMap.forEach {
            if (it.key.remote) {
                eventList.add(it.key)
            }
        }
        return eventList
    }
}

class UnSupportRegisterParam(message: String?) : Exception(message)