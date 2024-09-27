package com.example.opsc_quizcore.Models

data class QuizModel(
    val Name : String,
    val Category : String,
    val Questions : List<QuestionModel>
)
