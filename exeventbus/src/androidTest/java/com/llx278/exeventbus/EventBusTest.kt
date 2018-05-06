package com.llx278.exeventbus

import android.util.Log
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventBusTest {

    @Before
    fun setUp() {

        Log.d("main","before SetUP!")

    }

    @Test
    fun registerTest() {

        val eventBus = EventBus()
        val subscriberTest = Subscriber1()
        eventBus.register(subscriberTest)
        Log.d("main",eventBus.subscribedMap.toString())
        eventBus.unRegister(subscriberTest)
        Log.d("main",eventBus.subscribedMap.toString())
        val kClass = EventParam::class
        Log.d("main","kclass is ${kClass.qualifiedName}")
    }

}