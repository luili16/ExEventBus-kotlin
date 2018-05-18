package com.llx278.exeventbus.test

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.llx278.exeventbus.EventBus
import com.llx278.exeventbus.IDebug
import com.llx278.exeventbus.Poster
import com.llx278.exeventbus.remote.EMPTY
import com.llx278.exeventbus.remote.IReceiver
import com.llx278.exeventbus.remote.IRouter
import com.llx278.exeventbus.remote.RouterService

/**
 * 以下的服务用来分别模拟一个单独的进程连接到RouteService上去，用来测试消息能否准确送达
 */


class Service1 : Service() {
    private lateinit var router: IRouter
    private var currentMsg: String = EMPTY
    override fun onCreate() {
        super.onCreate()
        val service = Intent(this, RouterService::class.java)
        val success = bindService(service, MyConnection(), Context.BIND_AUTO_CREATE)
        if (!success) {
            Log.d("main", "Service1 bind to RouteService failed!")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return Debug()
    }

    inner class MyConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("main", "Service1 onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("main", "Service1 onServiceConnected")
            if (service == null) {
                Log.d("main", "Service1 service is null")
                return
            }
            router = IRouter.Stub.asInterface(service)
            router.addReceiver(object : IReceiver.Stub() {
                override fun onMessageReceive(where: String, message: Bundle) {
                    val msg = message.getString("msg")
                    Log.d("main", "Service1 onMessageReceive form $where msg is $msg")
                    currentMsg = message.getString("msg")
                }
            })
        }
    }

    inner class Debug : IDebug.Stub() {

        override fun sendCmd(cmd: String?) {

        }

        override fun thisPid(): Int {
            return Process.myPid()
        }

        override fun sendMsg(address: String?, msg: String?) {
            Log.d("main", "Service1 sendMsg = '$msg' from service1 to $address")
            if (address == null) {
                return
            }
            val bundle = Bundle()
            bundle.putString("msg", msg)
            router.send(address, bundle)
        }

        override fun receivedMsg(): String {
            Log.d("main", "Service1 receiveMsg = $")
            return currentMsg
        }
    }
}

class Service2 : Service() {

    private lateinit var poster : Poster
    private lateinit var debug: Debug

    override fun onCreate() {
        super.onCreate()
        val eventBus = EventBus()
        eventBus.register(Subscriber1())
        eventBus.register(Subscriber2())
        poster = Poster(this,eventBus)
    }

    override fun onBind(intent: Intent?): IBinder {
        debug = Debug()
        return debug
    }

    private inner class Debug : IDebug.Stub() {
        override fun sendCmd(cmd: String?) {
            if ("stop" == cmd) {
                poster.clearUp()
            }
        }

        override fun sendMsg(address: String?, msg: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun receivedMsg(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun thisPid(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}


class Service3 : Service() {



    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
