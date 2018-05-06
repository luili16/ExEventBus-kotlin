package com.llx278.exeventbus

import android.util.Log

/**
 * 模拟的消息订阅者
 */
class Subscriber1 {

    /**
     * 一个参数为空，返回值也为空的订阅者
     */
    @Subscriber(tag = "void_parameter_void_return")
    fun eventMethod() {
        Log.d("main","进入EventMethod")
    }

    @Subscriber(tag = "one_parameter")
    fun eventMethod1(event: EventParam) {
        Log.d("main","进入EventMethod1")
    }
}

