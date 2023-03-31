package com.project.cafehub.model

import android.os.Parcel
import android.os.Parcelable

data class CafeWorkHours(var id:String?=null,
                         var cafeId:String?=null,
                         var sundayWorkHour:String?=null,
                         var mondayWorkHour:String?=null,
                         var tuesdayWorkHour:String?=null,
                         var wednesdayWorkHour:String?=null,
                         var thursdayWorkHour:String?=null,
                         var fridayWorkHour:String?=null,
                         var saturdayWorkHour:String?=null):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(cafeId)
        parcel.writeString(sundayWorkHour)
        parcel.writeString(mondayWorkHour)
        parcel.writeString(tuesdayWorkHour)
        parcel.writeString(wednesdayWorkHour)
        parcel.writeString(thursdayWorkHour)
        parcel.writeString(fridayWorkHour)
        parcel.writeString(saturdayWorkHour)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CafeWorkHours> {
        override fun createFromParcel(parcel: Parcel): CafeWorkHours {
            return CafeWorkHours(parcel)
        }

        override fun newArray(size: Int): Array<CafeWorkHours?> {
            return arrayOfNulls(size)
        }
    }
}