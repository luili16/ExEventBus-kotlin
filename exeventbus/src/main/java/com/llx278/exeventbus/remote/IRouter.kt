package com.llx278.exeventbus.remote

import android.os.Bundle
import android.os.IInterface

interface IRouter : IInterface {

    /**
     * 向其他进程发送消息
     * @param address 发送消息的地址
     */
    fun send(address:String,msg : Bundle)

    /**
     * 添加一个用来接收消息的回调接口
     */
    fun addReceiver(receiver:IReceiver)

    /**
     * 取消一个用来接收消息的回调接口
     */
    fun removeReceiver()

    /**
     * 获得连接到服务端的所有的进程
     */
    fun getAliveClient() : ArrayList<String>

}