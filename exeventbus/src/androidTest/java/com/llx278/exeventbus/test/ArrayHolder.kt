package com.llx278.exeventbus.test

import android.os.Parcel
import android.os.Parcelable

data class ArrayHolder(val array: ArrayList<EventParam>) : Parcelable {
    constructor(source: Parcel) : this(
            source.createTypedArrayList(EventParam.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(array)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArrayHolder> = object : Parcelable.Creator<ArrayHolder> {
            override fun createFromParcel(source: Parcel): ArrayHolder = ArrayHolder(source)
            override fun newArray(size: Int): Array<ArrayHolder?> = arrayOfNulls(size)
        }
    }
}