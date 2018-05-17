package com.llx278.exeventbus.execute

import java.util.concurrent.ExecutorService
import kotlin.reflect.KFunction

object PoolExecutor : Executor {

    private var executorService : ExecutorService? = null

    override fun execute(kFunction: KFunction<*>, paramObj: Any?, obj: Any) {

        if (executorService == null) {
            executorService = ThreadPoolProvider.provide()
        }

        executorService!!.execute {
            if (paramObj == null) {
                kFunction.call(obj)
            } else {
                kFunction.call(obj, paramObj)
            }
        }
    }

    override fun submit(kFunction: KFunction<*>, paramObj: Any?, obj: Any): Any? {

        if (executorService == null) {
            executorService = ThreadPoolProvider.provide()
        }

        return executorService!!.submit {
            if (paramObj == null) {
                kFunction.call(obj)
            } else {
                kFunction.call(obj, paramObj)
            }
        }.get()
    }

    override fun quit() {
        if (executorService != null) {
            executorService!!.shutdown()
        }
    }
}