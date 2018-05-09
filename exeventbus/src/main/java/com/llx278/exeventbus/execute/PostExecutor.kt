package com.llx278.exeventbus.execute

import kotlin.reflect.KFunction

object PostExecutor : Executor {

    override fun execute(kFunction: KFunction<*>, paramObj: Any, obj: Any) {
        kFunction.call(obj,paramObj)
    }

    override fun submit(kFunction: KFunction<*>, paramObj: Any, obj: Any): Any? {
        return kFunction.call(obj,paramObj)
    }

}