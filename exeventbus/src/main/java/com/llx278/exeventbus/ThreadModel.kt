package com.llx278.exeventbus

/**
 * 声明了订阅事件被执行时在哪个线程运行
 */
enum class ThreadModel(val threadName:String) {
    /**
     * 主线程执行
     */
    MAIN("main"),

    /**
     * 在发布订阅事件的哪个线程中执行
     */
    POST("publish"),

    /**
     * 在一个自定义的线程中执行
     */
    POOL("pool");

    val mThreadName: String = threadName
}