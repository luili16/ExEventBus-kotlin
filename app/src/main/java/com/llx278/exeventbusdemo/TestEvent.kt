package com.llx278.exeventbusdemo

import android.os.Parcel
import android.os.Parcelable

data class TestEvent(val param: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(param)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TestEvent> = object : Parcelable.Creator<TestEvent> {
            override fun createFromParcel(source: Parcel): TestEvent = TestEvent(source)
            override fun newArray(size: Int): Array<TestEvent?> = arrayOfNulls(size)
        }
    }
}