package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.opsc_quizcore.ApiService.RetrofitClient
import com.example.opsc_quizcore.Models.QuestionModel
import com.example.opsc_quizcore.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var quizName: String
    private var currentQuestionIndex = 0
    private lateinit var questions: List<QuestionModel>
    private lateinit var countDownTimer: CountDownTimer
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        applySavedTheme()

        quizName = intent.getStringExtra("quizName").toString()
        questions = intent.getParcelableArrayListExtra<QuestionModel>("questions") ?: emptyList()

        binding.questionResultTV.visibility = View.INVISIBLE

        if (questions.isNotEmpty()) {
            startQuestion(questions[currentQuestionIndex])
        } else {
            displayError("No questions available.")
        }
    }

    private fun startQuestion(question: QuestionModel) {
        binding.quizNameTV.text = quizName
        binding.questionTV.text = question.QuestionText // Corrected reference here
        binding.optionABtn.text = question.Answer_1
        binding.optionBBtn.text = question.Answer_2
        binding.optionCBtn.text = question.Answer_3
        binding.optionDBtn.text = question.Answer_4
        binding.questionResultTV.visibility = View.INVISIBLE

        val options = listOf(binding.optionABtn, binding.optionBBtn, binding.optionCBtn, binding.optionDBtn)
        options.forEach { button ->
            button.setOnClickListener {
                checkAnswer(button.text.toString(), question.CorrectAnswer)
                countDownTimer.cancel()
                afterClick()
            }
        }
        startCountDownTimer()
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(10000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished / 100).toInt()
                binding.secondsProgressBar.progress = progress
                binding.progressSecondsTV.text = (progress / 100).toString()
            }

            override fun onFinish() {
                binding.secondsProgressBar.progress = 0
                displayResult(false)
                proceedToNextQuestion()
            }
        }.start()
    }

    private fun afterClick() {
        binding.secondsProgressBar.progress = 0
        proceedToNextQuestion()
    }

    private fun proceedToNextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            startQuestion(questions[currentQuestionIndex])
        } else {
            showScore()
        }
    }

    private fun checkAnswer(selectedAnswer: String, correctAnswer: String) {
        if (selectedAnswer == correctAnswer) {
            score++
            displayResult(true)
        } else {
            displayResult(false)
            highlightIncorrectAnswer(selectedAnswer)
        }
    }

    private fun highlightIncorrectAnswer(selectedAnswer: String) {
        val options = listOf(binding.optionABtn, binding.optionBBtn, binding.optionCBtn, binding.optionDBtn)
        options.forEach { button ->
            if (button.text.toString() == selectedAnswer) {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            }
        }
    }

    private fun displayResult(result: Boolean) {
        binding.questionResultTV.visibility = View.VISIBLE
        binding.questionResultTV.text = if (result) "Correct!" else "Wrong!"
        binding.questionResultTV.setTextColor(ContextCompat.getColor(this, if (result) R.color.green else R.color.red))
    }

    private fun showScore() {
        val intent = Intent(this, ScoreDisplayActivity::class.java).apply {
            putExtra("score", score)
        }
        startActivity(intent)
        finish()
    }

    private fun applySavedTheme() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedTheme: String? = sharedPreferences.getString("theme", "Red")

        when (savedTheme) {
            "White" -> window.decorView.setBackgroundColor(Color.WHITE)
            "Blue" -> window.decorView.setBackgroundColor(Color.BLUE)
            "Green" -> window.decorView.setBackgroundColor(Color.GREEN)
            else -> window.decorView.setBackgroundColor(Color.RED) // Default theme
        }
    }

    private fun displayError(message: String) {
        binding.questionResultTV.visibility = View.VISIBLE
        binding.questionResultTV.text = message
        binding.questionResultTV.setTextColor(ContextCompat.getColor(this, R.color.red))
    }
}
