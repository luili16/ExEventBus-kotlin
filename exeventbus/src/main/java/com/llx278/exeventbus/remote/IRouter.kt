package com.llx278.exeventbus.remote

import android.os.Bundle
import android.os.IInterface

interface IRouter : IInterface {

    /**
     * 向其他进程发送消息
     * @param address 发送消息的地址
     */
    fun send(address:String,msg : Bundle)

    fun addReceiver()



}