package com.llx278.exeventbus.remote

import android.os.Process
import android.util.Log

/**
 * 用一个进程的唯一pid作为这个进程的地址
 */
class Address private constructor(private val pid: Int) {

    companion object {
        /**
         * 返回已当前进程pid的地址
         */
        fun toAddress() : Address {
            return Address(Process.myPid())
        }

        /**
         * 返回指定pid的地址
         */
        fun toAddress(pid : Int) : Address {
            return Address(pid)
        }

        /**
         * 由指定的字符串生成一个地址
         */
        fun toAddress(address:String) : Address? {
            val split = address.split(":")
            if (split.size != 2) {
                return null
            }

            val pid = try {
                 split[1].toInt()
            } catch (e : NumberFormatException) {
                Log.e("ExEventBus","illegal address($address)!",e)
                return null
            }
            return Address(pid)
        }
    }

    override fun toString(): String {
        return """pid:$pid"""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (pid != other.pid) return false

        return true
    }

    override fun hashCode(): Int {
        return pid
    }
}