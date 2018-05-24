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
        val isBroadcast = Address.isBroadcast(address)
        if (isBroadcast) {
            receiverMap.forEach {
                if (it.key != where) {
                    val receiver = it.value
                    receiver.onMessageReceive(where, msg)
                }
            }
        } else {
            val receiver = receiverMap[address]
                    ?: throw UnExceptedAddressException("unExceptedAddress : $address")
            receiver.onMessageReceive(where, msg)
        }
    }

    override fun addReceiver(receiver: IReceiver) {
        val where = Address.toAddress(Binder.getCallingPid()).toString()
        val thisAddress = Address.toAddress().toString()
        if (where == thisAddress) {
            Log.e("ExEventBus", "add Receiver in same process!")
            return
        }

        receiverMap[where] = receiver
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
                Log.e(TAG, "RouterService : binder is null!")
                receiverMap.remove(it.key)
                hasRemoved = true
            }
            if (!binder.pingBinder()) {
                Log.e(TAG, "RouterService : ping binder return false!")
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
 *
 * 注意，此服务只能运行在一个独立的进程中，也就是说在AndroidManifest.xml中这个Service要声明为一个
 * 独立的进程，如果不这样，则会导致其他的进程无法向此进程发送发送消息，从而导致莫名奇妙的bug。
 */
class RouterService : Service() {

    private val router = RouterImpl()

    override fun onBind(intent: Intent?): IBinder? {
        return router
    }
}

class UnExceptedAddressException(message: String?) : Exception(message)