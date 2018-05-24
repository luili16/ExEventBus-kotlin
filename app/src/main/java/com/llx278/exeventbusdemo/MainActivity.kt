package com.llx278.exeventbusdemo

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.llx278.exeventbus.ExEventBus
import com.llx278.exeventbus.Subscriber
import com.llx278.exeventbus.Type

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = Intent(applicationContext, RemoteService::class.java)
        startService(service)
        //remote_publish.text = getString(R.string.hello)
        remote_publish.setOnClickListener {
            start()
        }

        remote_stop.setOnClickListener {
            stop1()
        }

        method1.setOnClickListener {
            val tag = "remote_test"
            ExEventBus.remotePublish(tag = tag)
        }

        method2.setOnClickListener {
            val tag = "parameter_test_String"
            val returnType = String::class.qualifiedName!!
            val retVal = ExEventBus.remotePublish(tag = tag, returnType = returnType,timeout = 1000)
            Log.d("main","retVal is $retVal")
        }
    }


    fun start() {
        val service = Intent(applicationContext, RemoteService::class.java)
        service.putExtra("cmd", CMD_START)
        startService(service)
    }


    fun stop1() {
        val service = Intent(applicationContext, RemoteService::class.java)
        service.putExtra("cmd", CMD_STOP)
        startService(service)
    }

    override fun onResume() {
        super.onResume()
        Thread {
            val current = SystemClock.uptimeMillis()
            ExEventBus.register(this)
            val now = SystemClock.uptimeMillis()
            val elapsed = now - current
            Log.d("main", "register time : $elapsed")
        }.start()
    }

    override fun onPause() {
        super.onPause()
        Thread {
            ExEventBus.unRegister(this)
        }.start()
    }

    /**
     * 空参数，空返回值
     */
    @Subscriber(tag = "parameter_test", remote = true, type = Type.BLOCK)
    fun eventMethod1() {
        Log.d("main", "Subscriber2 : eventMethod1")
    }

    /**
     * 整形参数，整形返回值
     */
    @Subscriber(tag = "parameter_test_Int", remote = true, type = Type.BLOCK)
    fun eventMethod2(param: Int): Int {
        Log.d("main", "Subscriber2 : eventMethod1 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_IntArray", remote = true, type = Type.BLOCK)
    fun eventMethod3(param: IntArray): IntArray {
        Log.d("main", "Subscriber2 : eventMethod3 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_Byte", remote = true, type = Type.BLOCK)
    fun eventMethod4(param: Byte): Byte {
        Log.d("main", "Subscriber2 : eventMethod4 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_ByteArray", remote = true, type = Type.BLOCK)
    fun eventMethod5(param: ByteArray): ByteArray {
        Log.d("main", "Subscriber2 : eventMethod5 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_Char", remote = true, type = Type.BLOCK)
    fun eventMethod6(param: Char): Char {
        Log.d("main", "Subscriber2 : eventMethod6 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_CharArray", remote = true, type = Type.BLOCK)
    fun eventMethod7(param: CharArray): CharArray {
        Log.d("main", "Subscriber2 : eventMethod7 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_Long", remote = true, type = Type.BLOCK)
    fun eventMethod9(param: Long): Long {
        Log.d("main", "Subscriber2 : eventMethod9 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_LongArray", remote = true, type = Type.BLOCK)
    fun eventMethod10(param: LongArray): LongArray {
        Log.d("main", "Subscriber2 : eventMethod10 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_test_String", remote = true, type = Type.BLOCK)
    fun eventMethod11(param: String): String {
        Log.d("main", "Subscriber2 : eventMethod11 param is $param")
        return param
    }

    @Subscriber(tag = "parameter_tes_Parcelable", remote = true, type = Type.BLOCK)
    fun eventMethod12(param: TestEvent): TestEvent {
        Log.d("main", "Subscriber2 : eventMethod12 param is $param")
        return param
    }

    /**
     * 这么做是为了模拟传送一个ArrayList，因为一些原因，没有实现直接发送ArrayList类型
     * 但可以这样间接的传送ArrayList
     */
    @Subscriber(tag = "parameter_tes_ArrayHolder", remote = true, type = Type.BLOCK)
    fun eventMethod14(param: ArrayListHolder): ArrayListHolder {
        Log.d("main", "Subscriber2 : eventMethod14 param is $param")
        return param
    }
}
