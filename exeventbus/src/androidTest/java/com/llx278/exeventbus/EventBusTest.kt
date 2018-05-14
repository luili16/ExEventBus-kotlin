package com.llx278.exeventbus

import android.util.Log
import com.llx278.exeventbus.test.EventParam
import com.llx278.exeventbus.test.Subscriber1
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventBusTest {

    @Before
    fun setUp() {
    }

    @Test
    fun registerTest() {

        val eventBus = EventBus()
        val subscriberTest = Subscriber1()
        eventBus.register(subscriberTest)
        Log.d("main", eventBus.subscribedMap.toString())
        eventBus.unRegister(subscriberTest)
        Log.d("main", eventBus.subscribedMap.toString())
        val kClass = EventParam::class
        Log.d("main", "kclass is ${kClass.qualifiedName}")
    }

    @Test
    fun simplePublishTest() {
        val eventBus = EventBus()
        val subscriberTest = Subscriber1()
        eventBus.register(subscriberTest)
        val eventObj = EventParam("a1", "a2")
        val tag = "one_parameter"
        eventBus.publish(eventObj = eventObj, tag = tag)

        val tag1 = "void_parameter_void_return"
        eventBus.publish(tag = tag1)

        val tag2 = "has_return"
        val returnType = String::class.qualifiedName!!
        val ret = eventBus.publish(eventObj,tag = tag2,returnType = returnType)
        Log.d("main","return value is $ret")
        eventBus.unRegister(subscriberTest)
    }

    @Test
    fun publishInDifferentThreads() {
        val eventBus = EventBus()
        val subscriberTest = Subscriber1()
        eventBus.register(subscriberTest)

        val tag = "different_threads"
        eventBus.publish(tag = tag)

        eventBus.unRegister(subscriberTest)

        eventBus.publish(tag = tag)
    }



}