package com.llx278.exeventbus

import kotlin.reflect.KClass

/**
 * 代表一个订阅事件
 * 这里面传入的方法的参数class和tag作为这个Event的唯一标志
 */
data class Event(val paramType : String?,
            val tag : String,
            val returnType : String,
            val remote : Boolean)