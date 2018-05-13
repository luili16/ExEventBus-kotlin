package com.llx278.exeventbus.remote

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import java.util.concurrent.ConcurrentHashMap


class RouterImpl : Router() {

    private val receiverMap = ConcurrentHashMap<String, IReceiver>()

    override fun send(address: String, msg: Bundle) {
        Log.d("main","RouterService send($address,$msg)")
        val where = Binder.getCallingPid().toString()
        msg.classLoader = this.javaClass.classLoader
        val isBroadcast = Address.isBroadcast(address)
        if (isBroadcast) {
            receiverMap.forEach {
                val receiver = it.value
                receiver.onMessageReceive(where = where, message = msg)
            }
        } else {
            receiverMap[where]?.onMessageReceive(where = where, message = msg)
        }
    }

    override fun addReceiver(receiver: IReceiver) {
        Log.d("main","RouterService addReceiver $receiver")
        val where = Binder.getCallingPid()
        receiverMap[where.toString()] = receiver
    }

    override fun removeReceiver() {
        Log.d("main","RouterService removeReceiver")
        val where = Binder.getCallingPid()
        receiverMap.remove(where.toString())
    }

    override fun getAliveClient(): ArrayList<String> {
        Log.d("main","RouterService getAvailableClient")
        val where = Binder.getCallingPid().toString()
        val addressList = ArrayList<String>()
        receiverMap.forEach {
            val receiver = it.value
            val binder = receiver.asBinder()
            var hasRemoved = false
            if (binder == null) {
                receiverMap.remove(it.key)
                hasRemoved = true
            }
            if (!binder.pingBinder()) {
                receiverMap.remove(it.key)
                hasRemoved = true
            }

            if (it.key != where && !hasRemoved) {
                addressList.add(it.key)
            }
        }
        return addressList
    }
}

/**
 * 此服务用来中转进程与进程间的数据交互
 */
class RouterService : Service() {

    private val router = RouterImpl()

    override fun onBind(intent: Intent?): IBinder {
        return router
    }
}