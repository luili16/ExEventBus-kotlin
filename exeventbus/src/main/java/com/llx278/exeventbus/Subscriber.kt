package com.llx278.exeventbus

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscriber(val type: Type,
                            val tag: String,
                            val threadModel: ThreadModel,
                            val remote: Boolean)