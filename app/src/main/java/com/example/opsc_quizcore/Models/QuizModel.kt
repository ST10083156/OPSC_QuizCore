package com.example.opsc_quizcore.Models

import android.os.Parcel
import android.os.Parcelable

data class QuizModel(
    val Name: String = "",
    val Category: String = "",
    val Questions: List<QuestionModel> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(QuestionModel.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Name)
        parcel.writeString(Category)
        parcel.writeTypedList(Questions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizModel> {
        override fun createFromParcel(parcel: Parcel): QuizModel {
            return QuizModel(parcel)
        }

        override fun newArray(size: Int): Array<QuizModel?> {
            return arrayOfNulls(size)
        }
    }
}
