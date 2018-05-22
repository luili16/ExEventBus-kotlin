package com.llx278.exeventbusdemo

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class ArrayListHolder(val array: ArrayList<TestEvent>) : Parcelable {
    constructor(source: Parcel) : this(
            source.createTypedArrayList(TestEvent.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(array)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArrayListHolder> = object : Parcelable.Creator<ArrayListHolder> {
            override fun createFromParcel(source: Parcel): ArrayListHolder = ArrayListHolder(source)
            override fun newArray(size: Int): Array<ArrayListHolder?> = arrayOfNulls(size)
        }
    }
}