package com.llx278.exeventbus

import android.content.Intent
import android.os.SystemClock
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.llx278.exeventbus.test.*
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ExEventBusTest {

    @Rule
    @JvmField
    val serviceTestRule = ServiceTestRule()

    private val executor = Executors.newFixedThreadPool(3)


    @Before
    fun before() {
    }

    @After
    fun after() {
    }

    /**
     * 对ExEventBus进行压力测试
     *
     *  开启4个进程用来接收从此进程发送过来的事件，测试消息可以正常的发送到指定的进程
     */
    @Test
    fun pressureTest() {

        // 启动服务进程
        val context = InstrumentationRegistry.getTargetContext()

        val service3Intent = Intent(context, Service3::class.java)
        val service3 = serviceTestRule.bindService(service3Intent)
        val service3Debugger =  IDebug.Stub.asInterface(service3)

        val service4Intent = Intent(context, Service4::class.java)
        val service4 = serviceTestRule.bindService(service4Intent)
        val service4Debugger = IDebug.Stub.asInterface(service4)


        val service5Intent = Intent(context, Service5::class.java)
        val service5 = serviceTestRule.bindService(service5Intent)
        val service5Debugger = IDebug.Stub.asInterface(service5)

        val service6Intent = Intent(context, Service6::class.java)
        val service6 = serviceTestRule.bindService(service6Intent)
        val service6Debugger = IDebug.Stub.asInterface(service6)

        // 等待10s，让进程完全的启动
        Thread.sleep(10 * 1000)
        val holderList = Array(3){
            when(it) {
                0 -> SubscriberHolder("no_return_method",
                        "hhh","kotlin.Unit","hhh")
                1-> SubscriberHolder("Subscriber3_block_return_String_method",
                        "ccc",
                        "kotlin.String",
                        "ccc")
                2 -> SubscriberHolder("Subscriber3_block_return_param_method",
                        EventParam("a","b"),
                        "com.llx278.exeventbus.test.EventParam",
                        "a")
                else -> throw RuntimeException("unKnow index : $it")
            }
        }

        ExEventBus.init(context)
        val timeout = 1000
        for (index : Int in 1..10000) {

            val i = index % 3
            executor.execute {
                val holder = holderList[i]
                if (i == 0) {
                    ExEventBus.remotePublish(holder.eventObj,holder.tag,holder.returnType,2000)
                    // 等待其他的进程执行结束
                    val endTime = SystemClock.uptimeMillis() + timeout
                    var hasReceived = false
                    while (SystemClock.uptimeMillis() < endTime) {
                        Thread.sleep(10)

                        val m3 = service3Debugger.receivedMsg(0)
                        Log.d("main","receive m3 : $m3")
                        val m4 = service4Debugger.receivedMsg(0)
                        Log.d("main","receive m4 : $m4")
                        val m5 = service5Debugger.receivedMsg(0)
                        Log.d("main","receive m5 : $m5")
                        val m6 = service6Debugger.receivedMsg(0)
                        Log.d("main","receive m6 : $m6")

                        if (m3 == holder.checkValue
                                && m4 == holder.checkValue
                                && m5 == holder.checkValue
                                && m6 == holder.checkValue) {
                            hasReceived = true
                            break
                        }
                    }
                    Assert.assertTrue(hasReceived)
                } else {
                    val retVal = ExEventBus.remotePublish(holder.eventObj,holder.tag,holder.returnType,2000)
                    Assert.assertNotNull(retVal)
                    if (i == 1) {
                        Assert.assertTrue(retVal is String)
                        Assert.assertEquals(holder.checkValue,retVal)
                    } else if (i == 2) {
                        Assert.assertTrue(retVal is EventParam)
                        Assert.assertEquals(holder.checkValue,(retVal as EventParam).p1)
                    }
                }
            }
        }

        executor.awaitTermination(20,TimeUnit.SECONDS)
        executor.shutdown()
        Thread.sleep(1000)
        service3Debugger.sendCmd("stop")
        service4Debugger.sendCmd("stop")
        service5Debugger.sendCmd("stop")
        service6Debugger.sendCmd("stop")
    }

    inner class SubscriberHolder(val tag: String,
                                 val eventObj: Any,
                                 val returnType: String,
                                 val checkValue: String)
}