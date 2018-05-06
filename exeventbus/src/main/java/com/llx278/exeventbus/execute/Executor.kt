package com.llx278.exeventbus.execute

import android.support.annotation.MainThread
import kotlin.reflect.KFunction

interface Executor {
    companion object {
        fun creator(threadModel: ThreadModel) : Executor {
            return when(threadModel) {
                ThreadModel.MAIN -> MainExecutor
                ThreadModel.POST -> PostExecutor
                ThreadModel.POOL -> PoolExecutor
            }
        }
    }

    fun execute(kFunction: KFunction<*>, paramObj: Any, obj: Any)
    fun submit(kFunction: KFunction<*>, paramObj: Any, obj: Any): Any?
}