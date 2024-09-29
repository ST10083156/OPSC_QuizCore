package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_quizcore.ApiService.RetrofitClient
import com.example.opsc_quizcore.Models.ApiResponse
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.QuestionModel
import com.example.opsc_quizcore.databinding.ActivityCreateQuizBinding
import com.example.opsc_quizcore.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var questionsList : MutableList<QuestionModel>
private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        questionsList = mutableListOf()
        db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        applySavedTheme()

        val categories = listOf("Sports","Entertainment","Animals","Geography","Other")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.categorySpinner.adapter=adapter


        binding.addQuestionBtn.setOnClickListener {
            if (questionsList.size == 10) {
                Toast.makeText(this, "Max 10 questions allowed!", Toast.LENGTH_SHORT).show()

            } else {
                if (checkInput()) {
                    val question = QuestionModel(
                        Question = binding.questionET.text.toString(),
                        Answer_1 = binding.optionAET.text.toString(),
                        Answer_2 = binding.optionBET.text.toString(),
                        Answer_3 = binding.optionCET.text.toString(),
                        Answer_4 = binding.optionDET.text.toString(),
                        CorrectAnswer = binding.correctAnswerET.text.toString()
                    )
                    questionsList.add(question)
                    Toast.makeText(
                        this,
                        "Question number ${questionsList.count()} added!",
                        Toast.LENGTH_SHORT
                    ).show()
                    clearInput()

                } else {
                    Toast.makeText(this, "Questions Cleared!", Toast.LENGTH_SHORT).show()
                    clearInput()
                }
            }

        }
        binding.clearQuestionsBtn.setOnClickListener {
            questionsList.clear()
            Toast.makeText(this, "Questions Cleared!", Toast.LENGTH_SHORT).show()

        }
        binding.doneBtn.setOnClickListener{

            val customQuiz = CustomQuizModel(
                    UserID = auth.uid.toString(),
                    QuizName = binding.nameET.text.toString(),
                    Category = binding.categorySpinner.selectedItem.toString(),
                    Questions = questionsList
            )

            db.collection("CustomQuizzes").add(customQuiz).addOnSuccessListener {
                Toast.makeText(this@CreateQuizActivity, "Quiz created successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CreateQuizActivity,QuizActivity::class.java)
                startActivity(  intent)
                finish()
            }

            /*RetrofitClient.instance.createQuiz(customQuiz).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val quizId = response.body()?.quizId
                        Toast.makeText(this@CreateQuizActivity, "Quiz created successfully! ID : ${response.body()?.quizId}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CreateQuizActivity,QuizActivity::class.java)
                        startActivity(  intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@CreateQuizActivity,
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@CreateQuizActivity, "Failed to create quiz: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })*/


            }
    }

    private fun applySavedTheme() {
        // Retrieve saved theme from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedTheme: String? = sharedPreferences.getString("theme", "Red") // Default to Red if not found

        // Set background color based on saved theme
        when (savedTheme) {
            "White" -> window.decorView.setBackgroundColor(Color.WHITE)
            "Blue" -> window.decorView.setBackgroundColor(Color.BLUE)
            "Green" -> window.decorView.setBackgroundColor(Color.GREEN)
        }
    }
        fun checkInput(): Boolean{

            if(binding.questionET.text.isEmpty()||
                binding.optionAET.text.isEmpty()||
                binding.nameET.text.isEmpty()||
                binding.optionBET.text.isEmpty()||
                binding.optionCET.text.isEmpty()||
                binding.optionDET.text.isEmpty()||
                binding.correctAnswerET.text.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return false
            }
            else
            {
                return true
            }

        }

        fun clearInput()
        {
            binding.questionET.text.clear()
            binding.optionAET.text.clear()
            binding.optionBET.text.clear()
            binding.optionCET.text.clear()
            binding.optionDET.text.clear()
            binding.correctAnswerET.text.clear()
        }
}

