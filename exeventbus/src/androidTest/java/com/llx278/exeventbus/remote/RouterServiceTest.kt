package com.llx278.exeventbus.remote

import android.content.Intent
import android.os.Bundle
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ServiceTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouterServiceTest {

    @Rule
    @JvmField
    public val serviceTestRule  = ServiceTestRule()

    @Before
    fun setUp() {

    }

    @After
    fun cleanUp() {

    }

    @Test
    fun testService() {
        val context = InstrumentationRegistry.getTargetContext()
        val routeIntent = Intent(context,RouterService::class.java)
        val routeBinder = serviceTestRule.bindService(routeIntent)
        val router = Router.asInterface(routeBinder)
        router.addReceiver(ReceiverImpl())
    }
}

class ReceiverImpl : Receiver() {
    override fun onMessageReceive(where: String, message: Bundle) {
        Log.d("main","onMessageReceive : where = $where message = $message")
    }
}