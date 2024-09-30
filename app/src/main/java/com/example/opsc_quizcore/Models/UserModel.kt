package com.example.opsc_quizcore.Models

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    val ID: String = "",            // Default value for ID
    val Name: String = "",          // Default value for Name
    val Username: String = "",      // Default value for Username
    val Image: String? = null,      // Default value for Image
    val Score: Int = 0              // Default value for Score
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
