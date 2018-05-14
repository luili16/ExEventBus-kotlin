package com.llx278.exeventbus

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.runner.intent.IntentCallback
import com.llx278.exeventbus.test.EventParam
import com.llx278.exeventbus.test.Service2
import com.llx278.exeventbus.test.Subscriber1
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosterTest {

    @Rule
    @JvmField
    public val serviceTestRule = ServiceTestRule()

    /**
     * 测试进程向service1进程发送一个事件，
     */
    @Test
    fun postTest() {

        val context = InstrumentationRegistry.getTargetContext()

        val service2Intent = Intent(context,Service2::class.java)
        val service2Binder = serviceTestRule.bindService(service2Intent)
        val service2 = IDebug.Stub.asInterface(service2Binder)

        val eventBus = EventBus()
        val poster = Poster(context,eventBus)

        val tag = "remote_process"
        val eventObj = EventParam("event","param")

        poster.post(eventObj,tag,"kotlin.Unit",1000 * 5)
    }
}