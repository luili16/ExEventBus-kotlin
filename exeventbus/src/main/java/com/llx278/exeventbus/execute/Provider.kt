package com.llx278.exeventbus.execute

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 子类实现这个类来提供自定义的线程池实现
 *
 */
abstract class Provider {

    companion object {
        private var injectedProvider : Provider? = null

        /**
         * 为PoolExecutor提供一个可替换的线程池，通过实现抽象类ThreadPoolProvider的make方法
         * 来替换provider的默认实现
         *
         * @throws IllegalStateException 线程池只能注入一次
         */
        fun injectTo(provider : Provider) {
            if (injectedProvider != null) {
                throw IllegalStateException("provider is not null! you have injected a provider")
            }
            injectedProvider = provider
        }

        internal fun provide() : ExecutorService {
            if (injectedProvider != null) {
                return injectedProvider!!.make()
            }
            // 线程池的默认实现是这么考虑的：
            //
            return Executors.newCachedThreadPool()
        }
    }

    abstract fun make() : ExecutorService
}