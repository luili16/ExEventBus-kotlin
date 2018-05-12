package com.llx278.exeventbus.execute

import android.os.Handler
import android.os.Looper
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KFunction

object MainExecutor : Executor {

    private val handler : Handler = Handler(Looper.getMainLooper())

    override fun submit(kFunction: KFunction<*>, paramObj: Any?, obj: Any): Any? {
        val doneSignal = CountDownLatch(1)
        val syncRunner = SyncRunner(doneSignal,kFunction,paramObj,obj)
        handler.post(syncRunner)
        doneSignal.await()
        return syncRunner.returnValue
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
}