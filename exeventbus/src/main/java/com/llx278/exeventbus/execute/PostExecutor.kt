package com.llx278.exeventbus.execute

import kotlin.reflect.KFunction

object PostExecutor : Executor {
    override fun quit() {
    }

    override fun execute(kFunction: KFunction<*>, paramObj: Any?, obj: Any) {
        if (paramObj == null) {
            kFunction.call(obj)
        } else {
            kFunction.call(obj, paramObj)
        }

    }

    override fun submit(kFunction: KFunction<*>, paramObj: Any?, obj: Any): Any? {
        return if (paramObj == null) {
            kFunction.call(obj)
        } else {
            kFunction.call(obj, paramObj)
        }
    }
}