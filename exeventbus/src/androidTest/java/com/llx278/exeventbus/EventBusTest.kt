package com.llx278.exeventbus

import android.util.Log
import com.llx278.exeventbus.test.*
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventBusTest {

    private  val eventBus : EventBus = EventBus()
    private  val subscriberTest = Subscriber1()
    @Before
    fun setUp() {
        eventBus.register(subscriberTest)
    }

    @After
    fun cleanUp() {
        eventBus.unRegister(subscriberTest)
    }

    @Test
    fun unRegisterTest() {
        // 测试unRegister
        eventBus.unRegister(subscriberTest)

        val size = eventBus.subscribedMap.size
        Assert.assertEquals(0,size)
    }

    @Test
    fun simplePublishTest() {

        val eventObj = EventParam("a1", "a2")
        val tag = "one_parameter"
        eventBus.publish(eventObj = eventObj, tag = tag)

        val tag1 = "void_parameter_void_return"
        eventBus.publish(tag = tag1)

        val tag2 = "has_return"
        val returnType = String::class.qualifiedName!!
        val ret = eventBus.publish(eventObj,tag = tag2,returnType = returnType)
        Log.d("main","return value is $ret")
    }

    @Test
    fun publishInDifferentThreads() {
        val tag = "different_threads"
        eventBus.publish(tag = tag)
    }

    @Test
    fun publishToDifferentClass() {
        val subscriber3 = Subscriber3()
        val subscriber4 = Subscriber4()
        val subscriber5 = Subscriber5()
        val subscriber6 = Subscriber6()

        eventBus.register(subscriber3)
        eventBus.register(subscriber4)
        eventBus.register(subscriber5)
        eventBus.register(subscriber6)

        val tag = "no_return_method"
        eventBus.publish(tag = tag)
    }

    /**
     * 测试当执行订阅方法出现异常时，能否正常的抛出，在实现EventBus的过程中并没有
     * 去捕获这个异常，而是直接抛出
     */
    @Test
    fun exceptionTest() {
        val tag = "should_throw_exception"

        //eventBus.publish(tag = tag)
    }

    /**
     * 测试随机的，大量的发布订阅事件的稳定性
     */
    @Test
    fun presserTest() {



    }
}