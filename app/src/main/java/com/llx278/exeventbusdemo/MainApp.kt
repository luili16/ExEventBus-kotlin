package com.llx278.exeventbusdemo

import android.app.Application
import android.os.Process
import android.util.Log
import com.llx278.exeventbus.ExEventBus
import com.llx278.exeventbus.execute.ThreadPoolProvider

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Thread {
            Log.d("main", "MainApp init!! this pid : ${Process.myPid()}")
            // 初始化ExEventBus
            ExEventBus.init(this)
            // 提供一个自定义的进程池
            ThreadPoolProvider.injectTo(MyProvider())
        }.start()
    }
}