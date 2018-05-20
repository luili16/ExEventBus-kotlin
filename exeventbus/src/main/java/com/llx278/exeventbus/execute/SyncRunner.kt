package com.llx278.exeventbus.execute

import java.util.concurrent.CountDownLatch
import kotlin.reflect.KFunction

internal class SyncRunner(private val doneSignal: CountDownLatch,
                 private val kFunction: KFunction<*>,
                 private val paramObj: Any?,
                 private val obj: Any) : Runnable {

    internal var returnValue: Any? = null
        private set

    override fun run() {
        try {
            returnValue = if (paramObj == null) {
                kFunction.call(obj)
            } else {
                kFunction.call(obj, paramObj)
            }
        } finally {
            doneSignal.countDown()
        }
    }
}