package com.llx278.exeventbus.execute

import java.util.concurrent.CountDownLatch
import kotlin.reflect.KFunction

class SyncRunner(private val doneSignal: CountDownLatch,
                 private val kFunction: KFunction<*>,
                 private val paramObj: Any,
                 private val obj: Any) : Runnable {

    internal var returnValue:Any? = null

    override fun run() {
        returnValue = kFunction.call(obj,paramObj)
        doneSignal.countDown()
    }
}