package com.llx278.exeventbus.test

import android.os.Parcel
import android.os.Parcelable

data class EventParam(val p1: String, val p2: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(p1)
        writeString(p2)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<EventParam> = object : Parcelable.Creator<EventParam> {
            override fun createFromParcel(source: Parcel): EventParam = EventParam(source)
            override fun newArray(size: Int): Array<EventParam?> = arrayOfNulls(size)
        }
    }
}