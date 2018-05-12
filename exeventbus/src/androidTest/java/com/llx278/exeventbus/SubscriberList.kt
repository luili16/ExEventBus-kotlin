package com.llx278.exeventbus

import android.util.Log
import com.llx278.exeventbus.execute.ThreadModel

/**
 * 模拟的消息订阅者
 */
class Subscriber1 {

    /**
     * 一个参数为空，返回值也为空的订阅者
     */
    @Subscriber(tag = "void_parameter_void_return")
    fun eventMethod() {
        Log.d("main", "进入EventMethod event is : empty!")
    }

    @Subscriber(tag = "one_parameter")
    fun eventMethod1(event: EventParam) {
        Log.d("main", "进入EventMethod1 event is : $event")
    }

    @Subscriber(tag = "has_return", type = Type.BLOCK_RETURN)
    fun eventMethod2(event: EventParam): String {
        Log.d("main", "进入eventMethod2 event is $event now return hello")
        return "hello"
    }

    @Subscriber(tag = "different_threads",threadModel = ThreadModel.MAIN)
    fun eventMethod3() {
        Log.d("main","running in main thread")
    }

    @Subscriber(tag = "different_threads",threadModel = ThreadModel.HANDLER)
    fun eventMethod4() {
        Log.d("main","running in handler thread")
    }

    @Subscriber(tag = "different_threads",threadModel = ThreadModel.POST)
    fun eventMethod5() {
        Log.d("main","running in post thread")
    }

    @Subscriber(tag = "different_threads",threadModel = ThreadModel.POOL)
    fun eventMethod6() {
        Log.d("main","running in pool thread")
    }
}

