package com.llx278.exeventbus

import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.declaredFunctions

class EventBus {
    private val tag = "ExEventBus"
    internal val subscribedMap: MutableMap<Event, CopyOnWriteArrayList<Subscription>> = ConcurrentHashMap()

    fun register(subscriber: Any) {

        val kClass = subscriber::class
        val iterator = kClass.declaredFunctions.iterator()
        while (iterator.hasNext()) {
            val kFunction = iterator.next()
            val annotations = kFunction.annotations
            val subsAnnotation = annotations.find { it is Subscriber }
            // 找到了一个Subscriber
            if (subsAnnotation != null && subsAnnotation is Subscriber) {
                // 判断函数参数，只允许函数有一个参数
                val size = kFunction.parameters.size
                if (size >= 3) {
                    Log.e(tag, "$kFunction : index error,subScribe method permit only one or empty parameter")
                    continue
                }
                var paramType: String
                paramType = when (size) {
                    1 -> "kotlin.Unit"
                    2 -> kFunction.parameters[1].type.toString()
                    else -> throw RuntimeException("this should never happen!")
                }
                Log.d("main","paramType is $paramType")
                val returnType = kFunction.returnType.toString()
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
     */
    fun publish(eventObj: Any, tag: String,returnType : String = "kotlin.Unit",remote : Boolean = false): Any {



        return 1
    }
}