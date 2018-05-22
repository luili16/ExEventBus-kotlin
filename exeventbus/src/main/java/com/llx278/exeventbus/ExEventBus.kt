package com.llx278.exeventbus

import android.content.Context

/**
 * ExEventBus实现了进程间消息的发布/订阅
 */
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
     * 订阅一个事件
     *
     * 注意： 同一个订阅事件不要重复注册，这样会导致一个订阅事件会被执行多次
     *
     * @throws IllegalStateException 当订阅方法的返回值不为空，并且type=Type.DEFAULT的时候会抛出此异常，因为
     * 当一个订阅方法有返回值的时候则需要将返回值返回去，这意味着需要阻塞当前的线程
     */
    fun register(subscriber: Any) {
        eventBus.register(subscriber)
    }


    fun unRegister(subscriber: Any) {
        eventBus.unRegister(subscriber)
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

    /**
     * 测试是否有某个进程存在指定的事件
     */
    fun pingRemote(tag: String, returnType: String, paramType: String): Boolean {
        return poster.pingRemote(tag, returnType, paramType)
    }

    /**
     * 仅测试用,因为正常使用是不会解除远程服务的绑定的，直到进程挂掉(这种情况会系统会抛一个远程服务泄露的异常).
     */
    internal fun internalClear() {
        poster.clearUp()
    }
}