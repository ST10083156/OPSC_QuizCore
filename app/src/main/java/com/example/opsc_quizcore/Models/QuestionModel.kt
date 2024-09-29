package com.example.opsc_quizcore.Models

import android.os.Parcel
import android.os.Parcelable

data class QuestionModel(
    val Question: String = "",
    val Answer_1: String = "",
    val Answer_2: String= "",
    val Answer_3: String= "",
    val Answer_4: String= "",
    val CorrectAnswer: String= ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Question)
        parcel.writeString(Answer_1)
        parcel.writeString(Answer_2)
        parcel.writeString(Answer_3)
        parcel.writeString(Answer_4)
        parcel.writeString(CorrectAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionModel> {
        override fun createFromParcel(parcel: Parcel): QuestionModel {
            return QuestionModel(parcel)
        }

        override fun newArray(size: Int): Array<QuestionModel?> {
            return arrayOfNulls(size)
        }
    }
}
