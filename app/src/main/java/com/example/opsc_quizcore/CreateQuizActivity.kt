package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_quizcore.Models.ApiResponse
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.QuestionModel
import com.example.opsc_quizcore.databinding.ActivityCreateQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var questionsList: MutableList<QuestionModel>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        questionsList = mutableListOf()
        enableEdgeToEdge()
        setContentView(binding.root)

        applySavedTheme()

        val categories = listOf("Sports", "Entertainment", "Animals", "Geography", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.addQuestionBtn.setOnClickListener {
            if (questionsList.size >= 10) {
                Toast.makeText(this, "Max 10 questions allowed!", Toast.LENGTH_SHORT).show()
            } else {
                if (checkInput()) {
                    val question = QuestionModel(
                        QuestionText = binding.questionET.text.toString(),
                        Answer_1 = binding.optionAET.text.toString(),
                        Answer_2 = binding.optionBET.text.toString(),
                        Answer_3 = binding.optionCET.text.toString(),
                        Answer_4 = binding.optionDET.text.toString(),
                        CorrectAnswer = binding.correctAnswerET.text.toString()
                    )
                    questionsList.add(question)
                    Toast.makeText(this, "Question number ${questionsList.count()} added!", Toast.LENGTH_SHORT).show()
                    clearInput()
                } else {
                    Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.clearQuestionsBtn.setOnClickListener {
            questionsList.clear()
            Toast.makeText(this, "Questions Cleared!", Toast.LENGTH_SHORT).show()
        }

        binding.doneBtn.setOnClickListener {
            if (binding.nameET.text.isNullOrEmpty()) {
                Toast.makeText(this, "Quiz name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (questionsList.isEmpty()) {
                Toast.makeText(this, "Please add at least one question", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val customQuiz = CustomQuizModel(
                Id = 0,
                UserID = auth.currentUser?.uid ?: "",
                QuizName = binding.nameET.text.toString(),
                Category = binding.categorySpinner.selectedItem.toString(),
                Questions = questionsList.toList()
            )

            firestore.collection("quizzes")
                .add(customQuiz)
                .addOnSuccessListener { documentReference ->
                    val quizId = documentReference.id
                    Toast.makeText(this@CreateQuizActivity, "Quiz created successfully! ID: $quizId", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@CreateQuizActivity, QuizActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@CreateQuizActivity, "Error adding quiz: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun applySavedTheme() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedTheme: String? = sharedPreferences.getString("theme", "Red")

        when (savedTheme) {
            "White" -> window.decorView.setBackgroundColor(Color.WHITE)
            "Blue" -> window.decorView.setBackgroundColor(Color.BLUE)
            "Green" -> window.decorView.setBackgroundColor(Color.GREEN)
            else -> window.decorView.setBackgroundColor(Color.RED)
        }
    }

    private fun checkInput(): Boolean {
        return binding.questionET.text.isNotEmpty() &&
                binding.optionAET.text.isNotEmpty() &&
                binding.optionBET.text.isNotEmpty() &&
                binding.optionCET.text.isNotEmpty() &&
                binding.optionDET.text.isNotEmpty() &&
                binding.correctAnswerET.text.isNotEmpty() &&
                binding.nameET.text.isNotEmpty()
    }

    private fun clearInput() {
        binding.questionET.text.clear()
        binding.optionAET.text.clear()
        binding.optionBET.text.clear()
        binding.optionCET.text.clear()
        binding.optionDET.text.clear()
        binding.correctAnswerET.text.clear()
    }
}
