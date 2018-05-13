package com.llx278.exeventbus.remote

import android.os.Bundle
import android.os.IInterface

/**
 * 消息接收进程的接口
 */
interface IReceiver : IInterface {
    /**
     * 当一个进程收到了这个消息，会通过此方法回调
     */
    fun onMessageReceive(where: String, message: Bundle)
}