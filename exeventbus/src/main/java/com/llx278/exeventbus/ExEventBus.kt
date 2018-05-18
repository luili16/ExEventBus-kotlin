package com.llx278.exeventbus

import android.content.Context

object ExEventBus {

    private val eventBus: EventBus = EventBus()

    private lateinit var poster: Poster

    /**
     * 在发布事件之前一定要调用此方法
     */
    fun init(context: Context) {
        poster = Poster(context, eventBus)
    }

    /**
     * 进程内发布订阅事件，忽略返回值
     *
     * @param eventObj 订阅方法参数
     * @param tag 订阅方法的标志
     *
     */
    fun publish(eventObj: Any? = null, tag: String) {
        eventBus.publish(eventObj, tag)
    }

    /**
     * 进程内发布订阅事件，等待返回值
     *
     * @param eventObj 订阅方法的参数
     * @param tag 订阅方法的标志
     * @param returnType 订阅方法的返回值类型
     */
    fun publish(eventObj: Any? = null, tag: String, returnType: String): Any? {
        return eventBus.publish(eventObj, tag, returnType)
    }

    /**
     * 将此订阅事件发布到其他进程里面去，忽略返回值
     */
    fun remotePublish(eventObj: Any?, tag: String, timeout: Long = 2000) {
        poster.post(eventObj, tag, "kotlin.Unit", timeout)
    }

    /**
     * 将此订阅事件发送到其他进程里面去，等待返回值
     */
    fun remotePublish(eventObj: Any?, tag: String, returnType: String, timeout: Long): Any? {
        return poster.post(eventObj, tag, returnType, timeout)
    }
}