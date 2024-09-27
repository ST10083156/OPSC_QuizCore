package com.example.opsc_quizcore

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
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
        val categories = listOf("Sports","Entertainment","Animals","Geography","Other")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.categorySpinner.adapter=adapter


        binding.addQuestionBtn.setOnClickListener {
            if (questionsList.count() == 10) {
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

        }
    }
        fun checkInput(): Boolean{

            if(binding.questionET.text.isEmpty()||
                binding.optionAET.text.isEmpty()||
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

