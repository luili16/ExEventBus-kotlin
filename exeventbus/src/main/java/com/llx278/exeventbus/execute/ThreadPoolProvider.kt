package com.llx278.exeventbus.execute

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 子类实现这个类来提供自定义的线程池实现
 *
 */
abstract class ThreadPoolProvider {

    companion object {
        private var injectedProvider : ThreadPoolProvider? = null

        /**
         * 为PoolExecutor提供一个线程池，通过实现抽象类ThreadPoolProvider的make方法
         * 来替换provider的默认实现
         *
         * @throws IllegalStateException 线程池只能注入一次
         */
        fun injectTo(provider : ThreadPoolProvider) {
            if (injectedProvider != null) {
                throw IllegalStateException("provider is not null! you have injected a provider")
            }
            injectedProvider = provider
        }

        internal fun provide() : ExecutorService {
            if (injectedProvider != null) {
                return injectedProvider!!.make()
            }

            return Executors.newFixedThreadPool(2)
        }
    }

    abstract fun make() : ExecutorService
}