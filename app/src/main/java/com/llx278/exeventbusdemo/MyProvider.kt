package com.llx278.exeventbusdemo

import com.llx278.exeventbus.execute.ThreadPoolProvider
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyProvider : ThreadPoolProvider() {

    override fun make(): ExecutorService {
        // 这里根据需求添加一个自定义的线程池
        return Executors.newFixedThreadPool(2)
    }
}