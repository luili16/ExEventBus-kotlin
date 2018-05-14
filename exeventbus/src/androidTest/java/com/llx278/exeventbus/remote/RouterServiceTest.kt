package com.llx278.exeventbus.remote

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.llx278.exeventbus.IDebug
import com.llx278.exeventbus.test.Service1
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouterServiceTest {
    var currentMsg: String = EMPTY
    @Rule
    @JvmField
    public val serviceTestRule = ServiceTestRule()

    @Before
    fun setUp() {
    }

    @After
    fun cleanUp() {
    }

    @Test
    fun testService() {
        val context = InstrumentationRegistry.getTargetContext()
        val routeIntent = Intent(context, RouterService::class.java)
        val routeBinder = serviceTestRule.bindService(routeIntent)
        val router = IRouter.Stub.asInterface(routeBinder)
        router.addReceiver(ReceiverImpl())

        val service1Intent = Intent(context, Service1::class.java)
        val service1Binder = serviceTestRule.bindService(service1Intent)
        val service1 = IDebug.Stub.asInterface(service1Binder)
        // service1进程向测试进程发送消息
        service1.sendMsg(Address.toAddress().toString(), "test service from service1")

        var endTime = SystemClock.uptimeMillis() + 5000
        while (SystemClock.uptimeMillis() < endTime) {
            Thread.sleep(1000)
            if (currentMsg != EMPTY) {
                break
            }
        }

        assertEquals("test service from service1",currentMsg)

        // 测试进程向service进程发送消息
        val address = Address.toAddress(service1.thisPid())
        service1.sendMsg(address.toString(),"test service from main")

        var testMsg = EMPTY
        endTime = SystemClock.uptimeMillis() + 5000
        while (SystemClock.uptimeMillis() < endTime) {
            Thread.sleep(1000)
            testMsg = service1.receivedMsg()
            if (testMsg != EMPTY) {
                break
            }
        }

        assertEquals("test service from main",testMsg)

        serviceTestRule.unbindService()
    }

    inner class ReceiverImpl : IReceiver.Stub() {
        override fun onMessageReceive(where: String?, message: Bundle?) {
            val msg = message?.getString("msg")
            currentMsg = msg ?: "empty"
            Log.d("main", "main process onMessageReceive $where and $msg")
        }
    }
}
