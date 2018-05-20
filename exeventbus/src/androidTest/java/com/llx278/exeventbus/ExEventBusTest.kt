package com.llx278.exeventbus

import android.content.Intent
import android.os.SystemClock
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.llx278.exeventbus.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

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
     *
     *
     */
    @Test
    fun pressureTest() {

        // 启动服务进程
        val context = InstrumentationRegistry.getTargetContext()

        val service3Intent = Intent(context, Service3::class.java)
        val service3Debugger = serviceTestRule.bindService(service3Intent)

        val service4Intent = Intent(context, Service4::class.java)
        val service4Debugger = serviceTestRule.bindService(service4Intent)

        val service5Intent = Intent(context, Service5::class.java)
        val service5Debugger = serviceTestRule.bindService(service5Intent)

        val service6Intent = Intent(context, Service6::class.java)
        val service6Debugger = serviceTestRule.bindService(service6Intent)

        // 等待10s，让进程完全的启动
        Thread.sleep(10 * 1000)
        val holderList = Array(3){
            when(it) {
                0 -> SubscriberHolder("no_return_method",
                        "hhh","kotlin.Unit","hhh")
                1-> SubscriberHolder("block_return_String_method",
                        "ccc",
                        "kotlin.String",
                        "ccc")
                2 -> SubscriberHolder("block_return_param_method",
                        EventParam("a","b"),
                        "com.llx278.exeventbus.test.EventParam",
                        "a")
                else -> throw RuntimeException("unKnow index : $it")
            }
        }

        ExEventBus.init(context)
        val timeout = 1000
        for (index : Int in 1..100) {

            val i = index % 3
            Log.d("main","i = $i")
            executor.execute {
                Thread.sleep(5)

                val holder = holderList[i]
                if (i == 0) {
                    ExEventBus.remotePublish(holder.eventObj,holder.tag,holder.returnType,2000)
                    // 等待其他的进程执行结束
                    val endTime = SystemClock.uptimeMillis() + timeout
                    while (SystemClock.uptimeMillis() < endTime) {
                        Thread.sleep(10)


                    }
                } else {

                }


            }

        }
    }

    inner class SubscriberHolder(val tag: String,
                                 val eventObj: Any,
                                 val returnType: String,
                                 val checkValue: String)

}