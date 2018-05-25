package com.llx278.exeventbus

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.llx278.exeventbus.test.ArrayHolder
import com.llx278.exeventbus.test.EventParam
import com.llx278.exeventbus.test.Service2
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosterTest {

    @Rule
    @JvmField
    public val serviceTestRule = ServiceTestRule()

    private val context = InstrumentationRegistry.getTargetContext()!!

    private lateinit var debug: IDebug

    private lateinit var poster: Poster

    @Before
    fun before() {
        // 仅仅开启一个用来测试的其他进程
        val service2Intent = Intent(context, Service2::class.java)
        val binder = serviceTestRule.bindService(service2Intent)
        debug = IDebug.Stub.asInterface(binder)

        val eventBus = EventBus()
        poster = Poster(context, eventBus)

        // 这里需要等待其他的进程已经启动完毕，否则无法发送订阅消息
        Thread.sleep(1000)
    }

    @After
    fun after() {
        debug.sendCmd("stop")
        poster.clearUp(context)
    }

    // ------------------------------ 对传入的参数和返回值进行测试 -------------------------------------

    @Test
    fun paramUnitTest() {
        val tag = "parameter_test"
        poster.post(eventObj = null, tag = tag, returnType = "kotlin.Unit", timeout = 5000)
    }

    @Test
    fun paramIntTest() {
        val tag = "parameter_test_Int"
        val param: Int = 5000
        val returnType = Int::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(eventObj = param, tag = tag, returnType = returnType, timeout = 5000)
        assertTrue(retVal is Int)
        assertEquals(param, retVal)
    }

    @Test
    fun paramIntArrayTest() {
        val tag = "parameter_test_IntArray"
        var seed = 23
        val param = IntArray(10) {
            seed++
        }
        val returnType = IntArray::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, returnType, 5000)
        assertTrue(retVal is IntArray)
        assertArrayEquals(param, retVal as IntArray)
    }

    @Test
    fun paramByteTest() {
        val tag = "parameter_test_Byte"
        val param: Byte = 44
        val returnType = Byte::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, returnType, 5000)
        assertTrue(retVal is Byte)
        assertEquals(param, retVal as Byte)
    }

    @Test
    fun paramByteArrayTest() {
        val tag = "parameter_test_ByteArray"
        var seedByte: Byte = 44
        val param = ByteArray(5) {
            seedByte++
        }
        val returnType = ByteArray::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, returnType, 5000)
        assertTrue(retVal is ByteArray)
        assertArrayEquals(param, retVal as ByteArray)
    }

    @Test
    fun paramCharTest() {
        val tag = "parameter_test_Char"
        val param: Char = 'h'
        val returnType = Char::class.qualifiedName ?: "kotlin.Unit"
        val returnVal = poster.post(param, tag, returnType, 5000)
        assertTrue(returnVal is Char)
        assertEquals(param, returnVal as Char)
    }

    @Test
    fun paramCharArrayTest() {
        val tag = "parameter_test_CharArray"
        val param: CharArray = CharArray(5) {
            'm' + it
        }
        val returnType = CharArray::class.qualifiedName ?: "kotlin.Unit"
        val returnVal = poster.post(param, tag, returnType, 5000)
        assertTrue(returnVal is CharArray)
        assertArrayEquals(param, returnVal as CharArray)
    }

    @Test
    fun paramLongTest() {
        val tag = "parameter_test_Long"
        val param: Long = 35099
        val returnType = Long::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, returnType, 5000)
        assertTrue(retVal is Long)
        assertEquals(param, retVal as Long)
    }

    @Test
    fun paramLongArrayTest() {
        val tag = "parameter_test_LongArray"
        val param = LongArray(7) {
            78748 + it.toLong()
        }
        val retType = LongArray::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, retType, 5000)
        assertTrue(retVal is LongArray)
        assertArrayEquals(param, retVal as LongArray)
    }

    @Test
    fun paramStringTest() {
        val tag = "parameter_test_String"
        val param = "hello android!"
        val retType = String::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, retType, 5000)
        assertTrue(retVal is String)
        assertEquals(param, retVal as String)
    }

    @Test
    fun paramParcelableTest() {
        // 等待其他的进程启动完毕
        val tag = "parameter_tes_Parcelable"
        val param = EventParam("event", "param")
        val retType = EventParam::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, retType, 1000 * 5)
        Log.d("main", "retVal is ${retVal!!::class.qualifiedName}")
        assertTrue(retVal is EventParam)
        assertEquals(param.p1, (retVal as EventParam).p1)
        assertEquals(param.p2, retVal.p2)
    }

    @Test
    fun paramArrayHolderTest() {
        val tag = "parameter_tes_ArrayHolder"
        val eventParams = ArrayList<EventParam>()
        eventParams.add(EventParam("a", "b"))
        eventParams.add(EventParam("c", "d"))
        eventParams.add(EventParam("e", "f"))
        val param = ArrayHolder(eventParams)
        val returnType = ArrayHolder::class.qualifiedName ?: "kotlin.Unit"
        val retVal = poster.post(param, tag, returnType, 5000)
        assertTrue(retVal is ArrayHolder)
        assertEquals(param, retVal)
        Log.d("main", "ArrayHolder is $retVal")
    }
    // ------------------------------ (end) -------------------------------------

    /**
     * 测试调用的远程方法发生异常时候的现象
     *
     * 因为异常是发生在订阅事件的进程，那么就对发布事件的进程没有影响。那么，如果returnType不是kotlin.Unit的话
     * 那么因为订阅事件的进程的崩溃，则会导致发布事件的进程抛出超时异常
     */
    @Test(expected = TimeoutException::class)
    fun exceptionTest() {

        val tag = "exception_timeout_test"
        poster.post(eventObj = null, tag = tag, returnType = String::class.qualifiedName!!, timeout = 5000)
    }

}