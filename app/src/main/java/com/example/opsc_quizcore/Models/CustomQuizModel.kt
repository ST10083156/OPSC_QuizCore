package com.example.opsc_quizcore.Models

data class CustomQuizModel(
    val UserID : String,
    val QuizName : String,
    val Category : String,
    val Questions : List<QuestionModel>
)
