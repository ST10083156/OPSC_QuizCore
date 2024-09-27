package com.example.opsc_quizcore.Models

import android.os.Parcel
import android.os.Parcelable

data class CustomQuizModel(
    val UserID: String,
    val QuizName: String,
    val Category: String,
    val Questions: List<QuestionModel>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<QuestionModel>().apply {
            parcel.readList(this, QuestionModel::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(UserID)
        parcel.writeString(QuizName)
        parcel.writeString(Category)
        parcel.writeList(Questions)
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
