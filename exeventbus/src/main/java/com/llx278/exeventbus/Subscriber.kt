package com.llx278.exeventbus

import com.llx278.exeventbus.execute.ThreadModel

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscriber(val type: Type = Type.DEFAULT,
                            val tag: String,
                            val threadModel: ThreadModel = ThreadModel.MAIN,
                            val remote: Boolean = false)