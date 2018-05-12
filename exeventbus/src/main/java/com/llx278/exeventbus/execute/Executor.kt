package com.llx278.exeventbus.execute

import kotlin.reflect.KFunction

interface Executor {
    companion object {
        fun creator(threadModel: ThreadModel) : Executor {
            return when(threadModel) {
                ThreadModel.MAIN -> MainExecutor
                ThreadModel.POST -> PostExecutor
                ThreadModel.POOL -> PoolExecutor
                ThreadModel.HANDLER -> HandlerExecutor
            }
        }
    }

    fun execute(kFunction: KFunction<*>, paramObj: Any?, obj: Any)
    fun submit(kFunction: KFunction<*>, paramObj: Any?, obj: Any): Any?
}