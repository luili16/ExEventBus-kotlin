package com.llx278.exeventbus.execute

import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KFunction

object HandlerExecutor : Executor {
    override fun quit() {
    }

    private val handler : Handler

    init {
        val handlerThread = HandlerThread("handlerExecutor")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    override fun execute(kFunction: KFunction<*>, paramObj: Any?, obj: Any) {
        handler.post {
            if (paramObj == null) {
                kFunction.call(obj)
            } else {
                kFunction.call(obj,paramObj)
            }
        }
    }

    override fun submit(kFunction: KFunction<*>, paramObj: Any?, obj: Any): Any? {

        val doneSignal = CountDownLatch(1)
        val runner = SyncRunner(doneSignal,kFunction,paramObj,obj)
        handler.post(runner)
        doneSignal.await()
        return  runner.returnValue
    }

}