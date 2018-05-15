package com.llx278.exeventbus

import com.llx278.exeventbus.execute.ThreadModel

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscriber(
        /**
         * 如何执行这个订阅事件
         *
         * 注意: 如果订阅方法的返回值不是Unit，那么Type类型一定是BLOCK_RETURN,因为只有这样才能在订阅方法
         * 执行完成之后将得到的返回值返回给调用者。
         */
        val type: Type = Type.DEFAULT,
        /**
         * 订阅事件的标志
         */
        val tag: String,
        /**
         * 订阅的方法在那个线程执行，默认是在主线程
         */
        val threadModel: ThreadModel = ThreadModel.MAIN,
        /**
         * true 则意味着这个事件可以跨进程发布
         */
        val remote: Boolean = false)