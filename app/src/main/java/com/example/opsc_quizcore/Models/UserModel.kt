package com.example.opsc_quizcore.Models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    val ID: String,
    val Name: String,
    val Username: String,
    val Image: Uri?,
    val Score: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ID)
        parcel.writeString(Name)
        parcel.writeString(Username)
        parcel.writeParcelable(Image, flags)
        parcel.writeInt(Score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}
