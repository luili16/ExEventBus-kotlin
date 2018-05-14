package com.llx278.exeventbus

import android.os.Parcel
import android.os.Parcelable
import kotlin.reflect.KClass

/**
 * 代表一个订阅事件
 * 这里面传入的方法的参数class和tag作为这个Event的唯一标志
 */
data class Event(val paramType: String,
                 val tag: String,
                 val returnType: String,
                 val remote: Boolean) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(paramType)
        writeString(tag)
        writeString(returnType)
        writeInt((if (remote) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
            override fun createFromParcel(source: Parcel): Event = Event(source)
            override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
        }
    }
}