package com.llx278.exeventbus.execute

import java.util.concurrent.Executors
import kotlin.reflect.KFunction

object PoolExecutor : Executor {
    private var num = 1
    private const val threadName : String = "ExEventBus-pool_thread"
    private val executorService = Executors.newCachedThreadPool {
        if (num == Int.MAX_VALUE) {
            num = 0
        }
        Thread(it, threadName + num++)
    }!!

    override fun execute(kFunction: KFunction<*>, paramObj: Any, obj: Any) {
        executorService.execute {
            kFunction.call(obj,paramObj)
        }
    }

    override fun submit(kFunction: KFunction<*>, paramObj: Any, obj: Any): Any? {
        return executorService.submit { kFunction.call(obj,paramObj) }.get()
    }

}