package com.llx278.exeventbus.remote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder


private const val TAG = "ExEventBus"

class Transport(context : Context) : IRouter {

    lateinit var router : IRouter
    val receiver : ReceiverImpl = ReceiverImpl()
    lateinit var outerReceiver : IReceiver

    init {
        val routerIntent = Intent(context,RouterService::class.java)
        context.bindService(routerIntent,object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                router = IRouter.Stub.asInterface(service)
                router.addReceiver(receiver)
            }

        },Context.BIND_AUTO_CREATE)
    }

    override fun send(addrss: String?, msg: Bundle?) {
        router.send(addrss,msg)
    }

    override fun addReceiver(receiver: IReceiver?) {
        if (receiver != null) {
            outerReceiver = receiver
        }
    }

    override fun removeReceiver() {
        router.removeReceiver()
    }

    override fun getAliveClient(): MutableList<String> {
        return router.aliveClient
    }

    override fun asBinder(): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ReceiverImpl : IReceiver.Stub() {
        override fun onMessageReceive(where: String?, message: Bundle?) {
            outerReceiver.onMessageReceive(where,message)
        }

        override fun asBinder(): IBinder {
            return this
        }
    }
}