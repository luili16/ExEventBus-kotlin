package com.llx278.exeventbusdemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.llx278.exeventbus.ExEventBus
import com.llx278.exeventbus.Subscriber
import com.llx278.exeventbus.Type
import java.util.*

const val CMD_START = 1
const val CMD_STOP = 2

class RemoteService : Service(), Runnable {


    private var list: ArrayList<EventHolder> = ArrayList()
    private var seedByte: Byte = 4
    private var stop: Boolean = true

    override fun run() {
        val seedChar: Char = 'h'
        list.add(EventHolder(null, "parameter_test", "kotlin.Unit", "kotlin.Unit"))
        list.add(EventHolder(345, "parameter_test_Int", Int::class.qualifiedName!!, Int::class.qualifiedName!!))
        list.add(EventHolder(IntArray(3) { it }, "parameter_test_IntArray", IntArray::class.qualifiedName!!, IntArray::class.qualifiedName!!))
        val byteVal : Byte = 11
        list.add(EventHolder(byteVal, "parameter_test_Byte", Byte::class.qualifiedName!!, Byte::class.qualifiedName!!))
        list.add(EventHolder(ByteArray(4) {
            seedByte++
        }, "parameter_test_ByteArray", ByteArray::class.qualifiedName!!, ByteArray::class.qualifiedName!!))
        val charArrayVal : CharArray = CharArray(5) {
            'm' + it
        }
        list.add(EventHolder(charArrayVal, "parameter_test_CharArray", CharArray::class.qualifiedName!!, CharArray::class.qualifiedName!!))
        list.add(EventHolder(65535L, "parameter_test_Long", Long::class.qualifiedName!!, Long::class.qualifiedName!!))
        list.add(EventHolder(LongArray(4) { it.toLong() }, "parameter_test_LongArray", LongArray::class.qualifiedName!!, LongArray::class.qualifiedName!!))
        list.add(EventHolder("this is String", "parameter_test_String", String::class.qualifiedName!!, String::class.qualifiedName!!))
        list.add(EventHolder(TestEvent("testEvent"), "parameter_tes_Parcelable", TestEvent::class.qualifiedName!!, TestEvent::class.qualifiedName!!))

        val arrayList = ArrayList<TestEvent>()
        arrayList.add(TestEvent("test1"))
        arrayList.add(TestEvent("test2"))
        arrayList.add(TestEvent("test3"))
        list.add(EventHolder(ArrayListHolder(arrayList), "parameter_tes_ArrayHolder", ArrayListHolder::class.qualifiedName!!, ArrayListHolder::class.qualifiedName!!))

        while (!stop) {
            // 向主进程发型消息
            list.forEach {
                Thread.sleep(2000)
                if (stop) {
                    return
                }
                ExEventBus.remotePublish(it.eventObj, it.tag, it.returnType, 2000)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        ExEventBus.register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val cmd = intent!!.getIntExtra("cmd", -1)
        if (cmd == CMD_START) {
            stop = false
            Thread(this).start()
        } else if (cmd == CMD_STOP) {
            stop = true
        } else {
            return super.onStartCommand(intent, flags, startId)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private inner class EventHolder(val eventObj: Any?, val tag: String, val paramType: String, val returnType: String)


    @Subscriber(tag = "remote_test", remote = true)
    fun method1() {
        Log.d("main","RemoteService call method1")
    }

    @Subscriber(tag = "parameter_test_String", remote = true, type = Type.BLOCK)
    fun method2() : String {
        Log.d("main","RemoteService call method2")
        return "hello world"
    }
}