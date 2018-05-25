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

    @Subscriber(tag = "has_return", type = Type.BLOCK)
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

    @Subscriber(tag = "remote_process", remote = true, type = Type.BLOCK)
    fun eventMethod8(event: EventParam): String {
        Log.d("main", "eventMethod8 receive an remote event : $event")
        return event.p1
    }

    @Subscriber(tag = "should_throw_exception")
    fun eventMethod9() {
        val a = 1
        val b = 0

        val c = a / b
    }
}

/**
 * 测试订阅方法的基本消息类型
 */
class Subscriber2 {

    /**
     * 空参数，空返回值
     */
    @Subscriber(tag = "parameter_test", remote = true, type = Type.BLOCK)
    fun eventMethod1() {
        Log.d("main", "Subscriber2 : eventMethod1")
    }

    /**
     * 整形参数，整形返回值
     */
    @Subscriber(tag = "parameter_test_Int", remote = true, type = Type.BLOCK)
    fun eventMethod2(param: Int): Int {
        Log.d("main", "Subscriber2 : eventMethod1 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_IntArray", remote = true, type = Type.BLOCK)
    fun eventMethod3(param: IntArray): IntArray {
        Log.d("main", "Subscriber2 : eventMethod3 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_Byte", remote = true, type = Type.BLOCK)
    fun eventMethod4(param: Byte): Byte {
        Log.d("main", "Subscriber2 : eventMethod4 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_ByteArray", remote = true, type = Type.BLOCK)
    fun eventMethod5(param: ByteArray): ByteArray {
        Log.d("main", "Subscriber2 : eventMethod5 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_Char", remote = true, type = Type.BLOCK)
    fun eventMethod6(param: Char): Char {
        Log.d("main", "Subscriber2 : eventMethod6 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_CharArray", remote = true, type = Type.BLOCK)
    fun eventMethod7(param: CharArray): CharArray {
        Log.d("main", "Subscriber2 : eventMethod7 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_Long", remote = true, type = Type.BLOCK)
    fun eventMethod9(param: Long): Long {
        Log.d("main", "Subscriber2 : eventMethod9 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_LongArray", remote = true, type = Type.BLOCK)
    fun eventMethod10(param: LongArray): LongArray {
        Log.d("main", "Subscriber2 : eventMethod10 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_String", remote = true, type = Type.BLOCK)
    fun eventMethod11(param: String): String {
        Log.d("main", "Subscriber2 : eventMethod11 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_tes_Parcelable", remote = true, type = Type.BLOCK)
    fun eventMethod12(param: EventParam): EventParam {
        Log.d("main", "Subscriber2 : eventMethod12 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_tes_ArrayHolder", remote = true, type = Type.BLOCK)
    fun eventMethod14(param: ArrayHolder): ArrayHolder {
        Log.d("main", "Subscriber2 : eventMethod14 param is $param")
        return param
    }

    /**
     * 此方法会抛出异常
     */
    @Subscriber(tag = "parameter_exception_test", remote = true, type = Type.BLOCK)
    fun eventMethod15() {
        val a = 1
        val b = 0

        val c = a / b
    }

    @Subscriber(tag = "exception_timeout_test", remote = true, type = Type.BLOCK)
    fun eventMethod16() : String {
        val a = 1
        val b = 0

        val c = a / b

        return "^_^"
    }
}

class Subscriber3 {

    var receivedMsg: String = ""

    @Subscriber(tag = "no_return_method", remote = true)
    fun method1(param: String) {
        Log.d("main", "call tag = 'no_return_method' in Subscriber3 method1")
        Thread.sleep(10)
        receivedMsg = param
    }

    @Subscriber(tag = "Subscriber3_block_return_String_method", type = Type.BLOCK, remote = true)
    fun method2(param: String): String {
        Log.d("main", "call tag = 'block_return_String_method' in Subscriber3 method2")
        Thread.sleep(10)
        return param
    }

    @Subscriber(tag = "Subscriber3_block_return_param_method", type = Type.BLOCK, remote = true)
    fun method3(param: EventParam): EventParam {
        Log.d("main", "call tag = 'block_return_param_method' in Subscriber3 method2")
        Thread.sleep(10)
        return param
    }
}

class Subscriber4 {
    var receivedMsg: String = ""
    @Subscriber(tag = "no_return_method", threadModel = ThreadModel.HANDLER, remote = true)
    fun method1(param: String) {
        Log.d("main", "call tag = 'no_return_method' in Subscriber4 method1")
        Thread.sleep(10)
        receivedMsg = param
    }

    @Subscriber(tag = "Subscriber4_block_return_String_method", type = Type.BLOCK, threadModel = ThreadModel.MAIN)
    fun method2(param: String): String {
        Log.d("main", "call tag = 'no_return_method' in Subscriber4 method1")
        Thread.sleep(10)
        return param
    }

    @Subscriber(tag = "Subscriber4_block_return_param_method", type = Type.BLOCK, threadModel = ThreadModel.POST)
    fun method3(param: EventParam): EventParam {
        Log.d("main", "Subscriber4 ")
        Thread.sleep(10)
        return param
    }
}

class Subscriber5 {
    var receivedMsg: String = ""
    @Subscriber(tag = "no_return_method", threadModel = ThreadModel.HANDLER,remote = true)
    fun method1(param: String) {
        Log.d("main", "call tag = 'no_return_method' in Subscriber5 method1")
        Thread.sleep(10)
        receivedMsg = param
    }

    @Subscriber(tag = "Subscriber5_block_return_String_method", type = Type.BLOCK, threadModel = ThreadModel.MAIN)
    fun method2(param: String): String {
        Log.d("main", "call tag = 'no_return_method' in Subscriber4 method1")
        Thread.sleep(10)
        return param
    }

    @Subscriber(tag = "Subscriber5_block_return_param_method", type = Type.BLOCK, threadModel = ThreadModel.POST)
    fun method3(param: EventParam): EventParam {
        Log.d("main", "Subscriber4 ")
        Thread.sleep(10)
        return param
    }
}

class Subscriber6 {
    var receivedMsg : String = ""
    @Subscriber(tag = "no_return_method", threadModel = ThreadModel.HANDLER,remote = true)
    fun method1(param: String) {
        Log.d("main", "call tag = 'no_return_method' in Subscriber6 method1")
        Thread.sleep(10)
        receivedMsg = param
    }

    @Subscriber(tag = "Subscriber6_block_return_String_method", type = Type.BLOCK, threadModel = ThreadModel.MAIN)
    fun method2(param: String): String {
        Log.d("main", "call tag = 'no_return_method' in Subscriber4 method1")
        Thread.sleep(10)
        return param
    }

    @Subscriber(tag = "Subscriber6_block_return_param_method", type = Type.BLOCK, threadModel = ThreadModel.POST)
    fun method3(param: EventParam): EventParam {
        Log.d("main", "Subscriber4 ")
        Thread.sleep(10)
        return param
    }
}

