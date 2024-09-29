package com.example.opsc_quizcore.Models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    var ID: String = "",
    var Name: String = "",
    var Username: String = "",
    var Image: String? = null,
    var Score: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ID)
        parcel.writeString(Name)
        parcel.writeString(Username)
        parcel.writeString(Image)  
        parcel.writeInt(Score)
    }


    fun getImageUri(): Uri? {
        return Image?.let { Uri.parse(it) }
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
