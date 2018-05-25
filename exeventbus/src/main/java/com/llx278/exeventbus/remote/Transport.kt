package com.llx278.exeventbus.remote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log


private const val TAG = "ExEventBus"

class Transport(context: Context) : IRouter {

    var router: IRouter? = null
    val receiver: ReceiverImpl = ReceiverImpl()
    lateinit var outReceiver: IReceiver
    private val conn: RouterConnection = RouterConnection()

    init {
        val routerIntent = Intent(context, RouterService::class.java)
        context.bindService(routerIntent, conn, Context.BIND_AUTO_CREATE)
    }

    override fun send(addrss: String?, msg: Bundle?) {
        if (router == null) {
            Log.e(TAG, "disconnected from RouterService!")
            return
        }

        router!!.send(addrss, msg)
    }

    fun destroy(context: Context) {
        if (router != null) {
            router!!.removeReceiver()
        }
        context.unbindService(conn)
        Log.i(TAG, "Transport : have disconnect with RouteService and current process is ${Process.myPid()}")
    }

    override fun addReceiver(receiver: IReceiver?) {
        if (receiver == null) {
            Log.e(TAG, "passing an empty receiver when call addReceiver(IReceiver) method")
            return
        }
        outReceiver = receiver
    }

    override fun removeReceiver() {
        if (router == null) {
            Log.e(TAG, "disconnected from RouterService")
            return
        }

        router!!.removeReceiver()
    }

    override fun getAliveClient(): MutableList<String> {
        if (router == null) {
            Log.e(TAG, "disconnected from RouterService")
            return ArrayList()
        }
        return router!!.aliveClient
    }

    override fun asBinder(): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ReceiverImpl : IReceiver.Stub() {
        override fun onMessageReceive(where: String?, message: Bundle?) {
            outReceiver.onMessageReceive(where, message)
        }

        override fun asBinder(): IBinder {
            return this
        }
    }

    inner class RouterConnection : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            router = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            router = IRouter.Stub.asInterface(service)
            if (router != null) {
                router!!.addReceiver(receiver)
            }
        }

    }
}