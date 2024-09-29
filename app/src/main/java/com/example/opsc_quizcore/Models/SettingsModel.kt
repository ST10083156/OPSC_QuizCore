package com.example.opsc_quizcore.Models

data class SettingsModel(
    val UserID : String,
    val Mode : String = "Light",
    val Theme : String = "Red",
    val Size: String = "Small"
)
