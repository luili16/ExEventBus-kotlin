package com.llx278.exeventbus.execute

import kotlin.reflect.KFunction

/**
 * 定义了订阅事件具体在哪个线程执行
 */
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

    /**
     * 执行一个指定的function
     */
    fun execute(kFunction: KFunction<*>, paramObj: Any?, obj: Any)

    /**
     * 执行一个指定的function，并将执行的结果返回
     * 注意： 这个方法会阻塞当前的线程，直到待执行的方法执行结束返回执行的结果，如果待执行的
     * 方法崩溃了，那么就返回null
     */
    fun submit(kFunction: KFunction<*>, paramObj: Any?, obj: Any): Any?

    /**
     * 清除当前Executor所持有的线程资源
     */
    fun quit()
}