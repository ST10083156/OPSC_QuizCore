package com.example.opsc_quizcore.Models

import android.os.Parcel
import android.os.Parcelable

data class QuizModel(
    val Name: String,
    val Category: String,
    val Questions: List<QuestionModel>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<QuestionModel>().apply {
            parcel.readList(this, QuestionModel::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Name)
        parcel.writeString(Category)
        parcel.writeList(Questions)
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
