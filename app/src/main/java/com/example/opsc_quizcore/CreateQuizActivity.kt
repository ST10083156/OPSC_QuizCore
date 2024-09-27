package com.example.opsc_quizcore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_quizcore.Models.QuestionModel
import com.example.opsc_quizcore.databinding.ActivityCreateQuizBinding
import com.example.opsc_quizcore.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class CreateQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var questionsList : MutableList<QuestionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        questionsList = mutableListOf()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)



    }
}