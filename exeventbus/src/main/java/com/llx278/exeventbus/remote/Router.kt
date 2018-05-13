package com.llx278.exeventbus.remote

import android.os.*

private const val ROUTER_DESCRIPTOR = "com.llx278.exeventbus.remote.IRouter"

private const val TRANSACTION_send = (IBinder.FIRST_CALL_TRANSACTION + 0)
private const val TRANSACTION_addReceiver = (IBinder.FIRST_CALL_TRANSACTION + 1)
private const val TRANSACTION_removeReceiver = (IBinder.FIRST_CALL_TRANSACTION + 2)
private const val TRANSACTION_getAliveClient = (IBinder.FIRST_CALL_TRANSACTION + 3)


abstract class Router() : Binder(), IRouter {

    init {
        this.attachInterface(this, ROUTER_DESCRIPTOR)
    }

    companion object {
        fun asInterface(obj: IBinder): IRouter {
            val iin: IInterface = obj.queryLocalInterface(ROUTER_DESCRIPTOR)
            if (iin is IRouter) {
                return iin
            }
            return RouterProxy(obj)
        }
    }

    override fun asBinder(): IBinder {
        return this
    }

    override fun onTransact(code: Int, data: Parcel?, reply: Parcel?, flags: Int): Boolean {

        return when (code) {
            IBinder.INTERFACE_TRANSACTION -> {
                reply!!.writeString(ROUTER_DESCRIPTOR)
                true
            }
            TRANSACTION_send -> {
                data!!.enforceInterface(ROUTER_DESCRIPTOR)
                val address = data.readString()
                val msg: Bundle = Bundle.CREATOR.createFromParcel(data)
                this.send(address, msg)
                reply!!.writeNoException()
                true
            }
            TRANSACTION_addReceiver -> {
                data!!.enforceInterface(ROUTER_DESCRIPTOR)
                val receiver : IReceiver = Receiver.asInterface(data.readStrongBinder())
                this.addReceiver(receiver)
                reply!!.writeNoException()
                true
            }
            TRANSACTION_removeReceiver-> {
                data!!.enforceInterface(ROUTER_DESCRIPTOR)
                this.removeReceiver()
                reply!!.writeNoException()
                true
            }
            TRANSACTION_getAliveClient -> {
                data!!.enforceInterface(ROUTER_DESCRIPTOR)
                val clients = getAliveClient()
                reply!!.writeNoException()
                reply.writeStringList(clients)
                true
            }
            else -> {
                super.onTransact(code, data, reply, flags)
            }
        }
    }
}

class RouterProxy(private val remote: IBinder) : IRouter {

    override fun send(address: String, msg: Bundle) {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInterfaceToken(ROUTER_DESCRIPTOR)
            data.writeString(address)
            msg.writeToParcel(data, 0)
            remote.transact(TRANSACTION_send, data, reply, 0)
            reply.readException()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    override fun addReceiver(receiver: IReceiver) {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInterfaceToken(ROUTER_DESCRIPTOR)
            data.writeStrongBinder(receiver.asBinder())
            remote.transact(TRANSACTION_addReceiver, data, reply, 0)
            reply.readException()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    override fun removeReceiver() {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()

        try {
            data.writeInterfaceToken(ROUTER_DESCRIPTOR)
            remote.transact(TRANSACTION_removeReceiver, data, reply, 0)
            reply.readException()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    override fun getAliveClient(): ArrayList<String> {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        val result: ArrayList<String>
        try {
            data.writeInterfaceToken(ROUTER_DESCRIPTOR)
            remote.transact(TRANSACTION_getAliveClient, data, reply, 0)
            reply.readException()
            result = reply.createStringArrayList()
        } finally {
            data.recycle()
            reply.recycle()
        }
        return result
    }

    override fun asBinder(): IBinder {
        return remote
    }
}