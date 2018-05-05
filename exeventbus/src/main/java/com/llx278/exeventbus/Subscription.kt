package com.llx278.exeventbus

import java.lang.ref.WeakReference
import kotlin.reflect.KFunction

data class Subscription(val subscribeRef : WeakReference<Any>,val threadModel : ThreadModel,val type: Type,val kFunc : KFunction<*>)