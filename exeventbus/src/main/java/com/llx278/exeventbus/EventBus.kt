package com.llx278.exeventbus

import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.declaredFunctions

class EventBus {
    val TAG = "ExEventBus"
    val subscribedMap: MutableMap<Event,CopyOnWriteArrayList<Subscription>> = ConcurrentHashMap()

    public fun register(subscriber : Any) {

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
                    Log.e(TAG,"$kFunction : index error,subScribe method permit only one or empty parameter")
                    continue
                }
                var paramType : String
                paramType = when(size) {
                    1 -> "kotlin.Unit"
                    2 -> kFunction.parameters[2].type.toString()
                    else -> throw RuntimeException("this should never happen!")
                }
                val returnType = kFunction.returnType.toString()
                val event = Event(paramType,subsAnnotation.tag,returnType,subsAnnotation.remote)
                // 找到此event对应的subscriber列表
                var subscriptionList : CopyOnWriteArrayList<Subscription>
                if (!subscribedMap.containsKey(event)) {
                    subscriptionList = CopyOnWriteArrayList()
                } else {
                    subscriptionList = subscribedMap.getValue(event)
                }

                val type = subsAnnotation.type
                val weakSubscriber : WeakReference<Any> = WeakReference(subscriber)
                val subscription = Subscription(weakSubscriber,subsAnnotation.threadModel,type,kFunction)
                // 返回值不为空的情况下，只能保证只有一个方法被执行，因为这个方法执行完毕后的值是需要返回去的
                // 这里只保证subscriptionList里只有一个元素
                if ("kotlin.Unit" != returnType && subscriptionList.isEmpty()) {
                    subscriptionList.add(subscription)
                } else {
                    subscriptionList.add(subscription)
                }
                subscribedMap.put(event,subscriptionList)
            }
        }
     }
}