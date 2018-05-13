package com.llx278.exeventbus.remote

import android.os.*


private const val RECEIVER_DESCRIPTOR = "com.llx278.exeventbus.remote.IReceiver"
private const val TRANSACTION_onMessageReceiver = (IBinder.FIRST_CALL_TRANSACTION + 0)

abstract class Receiver() : Binder(),IReceiver {

    init {
        this.attachInterface(this, RECEIVER_DESCRIPTOR)
    }

    companion object {
        fun asInterface(obj:IBinder) : IReceiver {
            val iin : IInterface = obj.queryLocalInterface(RECEIVER_DESCRIPTOR)
            if (iin is IReceiver) {
                return iin
            }
            return ReceiverProxy(obj)
        }
    }

    override fun asBinder(): IBinder {
        return this
    }

    override fun onTransact(code: Int, data: Parcel?, reply: Parcel?, flags: Int): Boolean {

        return when(code) {
            IBinder.INTERFACE_TRANSACTION-> {
                reply!!.writeString(RECEIVER_DESCRIPTOR)
                true
            }
            TRANSACTION_onMessageReceiver -> {
                data!!.enforceInterface(RECEIVER_DESCRIPTOR)
                val where = data.readString()
                val message : Bundle = Bundle.CREATOR.createFromParcel(data)
                this.onMessageReceive(where,message)
                reply!!.writeNoException()
                true
            }
            else -> {
                super.onTransact(code, data, reply, flags)
            }
        }
    }
}

class ReceiverProxy(private val remote : IBinder) : IReceiver {

    override fun onMessageReceive(where: String, message: Bundle) {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()

        try {

            data.writeInterfaceToken(RECEIVER_DESCRIPTOR)
            data.writeString(where)
            message.writeToParcel(data,0)
            remote.transact(TRANSACTION_onMessageReceiver,data,reply,0)
            reply.readException()
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    override fun asBinder(): IBinder {
        return remote
    }
}