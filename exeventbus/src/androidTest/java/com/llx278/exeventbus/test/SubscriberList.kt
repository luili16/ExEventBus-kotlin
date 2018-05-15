package com.llx278.exeventbus.test

import android.util.Log
import com.llx278.exeventbus.Subscriber
import com.llx278.exeventbus.Type
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

    @Subscriber(tag = "different_threads", threadModel = ThreadModel.MAIN)
    fun eventMethod3() {
        Log.d("main", "running in main thread")
    }

    @Subscriber(tag = "different_threads", threadModel = ThreadModel.HANDLER)
    fun eventMethod4() {
        Log.d("main", "running in handler thread")
    }

    @Subscriber(tag = "different_threads", threadModel = ThreadModel.POST)
    fun eventMethod5() {
        Log.d("main", "running in post thread")
    }

    @Subscriber(tag = "different_threads", threadModel = ThreadModel.POOL)
    fun eventMethod6() {
        Log.d("main", "running in pool thread")
    }

    @Subscriber(tag = "remote_process", remote = true)
    fun eventMethod7(event: EventParam) {
        Log.d("main", "eventMethod7 receive an remote event : $event")
    }

    @Subscriber(tag = "remote_process", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod8(event: EventParam): String {
        Log.d("main", "eventMethod8 receive an remote event : $event")
        return event.p1
    }
}

/**
 * 测试订阅方法的基本消息类型
 */
class Subscriber2 {

    /**
     * 空参数，空返回值
     */
    @Subscriber(tag = "parameter_test", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod1() {
        Log.d("main", "Subscriber2 : int eventMethod1")
    }

    /**
     * 整形参数，整形返回值
     */
    @Subscriber(tag = "parameter_test_Int", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod2(param: Int): Int {
        return param
    }

    @Subscriber(tag = "parameter_test_IntArray", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod3(param: IntArray): IntArray {
        return param
    }

    @Subscriber(tag = "parameter_test_Byte", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod4(param: Byte): Byte {
        return param
    }

    @Subscriber(tag = "parameter_test_ByteArray", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod5(param: ByteArray): ByteArray {
        return param
    }

    @Subscriber(tag = "parameter_test_Char", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod6(param: Char): Char {
        return param
    }

    @Subscriber(tag = "parameter_test_CharArray", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod7(param : CharArray) : CharArray {
        return param
    }

    @Subscriber(tag = "parameter_test_CharSequence", remote = true, type = Type.BLOCK_RETURN)
    fun eventMethod8(param : CharSequence) : CharSequence {
        return param
    }




}

