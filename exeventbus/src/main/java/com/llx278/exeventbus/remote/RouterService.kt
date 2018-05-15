package com.llx278.exeventbus.remote

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import com.llx278.exeventbus.Event
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ExEventBus"

class RouterImpl : IRouter.Stub() {

    private val receiverMap = ConcurrentHashMap<String, IReceiver>()

    override fun send(address: String, msg: Bundle) {
        val where = Address.toAddress(Binder.getCallingPid()).toString()
        // 找到可能拥有所有实现parcelable 或 putSerializable的对象，并重新设置classloader
        msg.keySet().forEach {
            val obj  = msg.get(it)
            if (obj != null && (obj is Parcelable || obj is Serializable)) {
                msg.classLoader = obj::class.java.classLoader
            }
        }
        //msg.classLoader = Event::class.java.classLoader
        val isBroadcast = Address.isBroadcast(address)
        if (isBroadcast) {
            receiverMap.forEach {
                val receiver = it.value
                receiver.onMessageReceive(where, msg)
            }
        } else {
            val receiver = receiverMap[address] ?: throw UnExceptedAddressException("unExceptedAddress : $address")
            receiver.onMessageReceive(where, msg)
        }
    }

    override fun addReceiver(receiver: IReceiver) {
        val where = Address.toAddress(Binder.getCallingPid())
        receiverMap[where.toString()] = receiver
    }

    override fun removeReceiver() {
        val where = Address.toAddress(Binder.getCallingPid())
        receiverMap.remove(where.toString())
    }

    override fun getAliveClient(): ArrayList<String> {
        val where = Address.toAddress(Binder.getCallingPid()).toString()
        val addressList = ArrayList<String>()
        receiverMap.forEach {
            val receiver = it.value
            val binder = receiver.asBinder()
            var hasRemoved = false
            if (binder == null) {
                Log.d(TAG,"RouterService : binder is null!")
                receiverMap.remove(it.key)
                hasRemoved = true
            }
            if (!binder.pingBinder()) {
                Log.d(TAG,"RouterService : ping binder return false!")
                receiverMap.remove(it.key)
                hasRemoved = true
            }

            if (it.key != where && !hasRemoved) {
                addressList.add(it.key)
            }
        }
        Log.d(TAG,"RouterService : receiverMap = $receiverMap")
        Log.d(TAG,"RouterService : addressList = $addressList")
        return addressList
    }
}


/**
 * 此服务用来中转进程与进程间的数据交互
 */
class RouterService : Service() {

    private val router = RouterImpl()

    override fun onBind(intent: Intent?): IBinder? {
        return router
    }
}

class UnExceptedAddressException(message: String?) : Exception(message)