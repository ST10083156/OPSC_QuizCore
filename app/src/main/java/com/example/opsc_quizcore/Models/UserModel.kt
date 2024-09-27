package com.example.opsc_quizcore.Models

import android.net.Uri

data class UserModel(
    val ID : String,
    val Name:String,
    val Username: String,
    val Image: Uri?,
    val Score:Int
)
