package com.llx278.exeventbus.execute

import kotlin.reflect.KFunction

object PoolExecutor : Executor {
    override fun execute(kFunction: KFunction<*>, paramObj: Any, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun submit(kFunction: KFunction<*>, paramObj: Any, obj: Any): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}