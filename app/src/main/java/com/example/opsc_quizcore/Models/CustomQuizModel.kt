package com.example.opsc_quizcore.Models

import android.os.Parcel
import android.os.Parcelable

data class CustomQuizModel(
    val Id: Int = 0,
    val UserID: String,
    val QuizName: String,
    val Category: String,
    val Questions: List<QuestionModel>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        Id = parcel.readInt(),
        UserID = parcel.readString() ?: "",
        QuizName = parcel.readString() ?: "",
        Category = parcel.readString() ?: "",
        Questions = parcel.createTypedArrayList(QuestionModel.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(Id)
        parcel.writeString(UserID)
        parcel.writeString(QuizName)
        parcel.writeString(Category)
        parcel.writeTypedList(Questions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomQuizModel> {
        override fun createFromParcel(parcel: Parcel): CustomQuizModel {
            return CustomQuizModel(parcel)
        }

        override fun newArray(size: Int): Array<CustomQuizModel?> {
            return arrayOfNulls(size)
        }
    }
}
