package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.example.opsc_quizcore.ApiService.RetrofitClient
import com.example.opsc_quizcore.Models.QuestionModel
import com.example.opsc_quizcore.Models.QuizModel
import com.example.opsc_quizcore.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth

class QuizActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQuizBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var quizName : String
    private var currentQuestionIndex = 0
    private lateinit var questions: List<QuestionModel>
    private lateinit var countDownTimer: CountDownTimer
    private var score : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityQuizBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        applySavedTheme()

        quizName = intent.getStringExtra("quizName").toString()
        questions = intent.getParcelableArrayListExtra("questions") ?: emptyList()
        binding.questionResultTV.visibility = View.INVISIBLE
        if (questions.isEmpty()) {
            Toast.makeText(this, "No questions available.", Toast.LENGTH_SHORT).show()
            finish() // Or redirect to another activity
        } else {
            startQuestion(questions[currentQuestionIndex])
        }




    }

    private fun startQuestion(question: QuestionModel) {
        binding.quizNameTV.text = quizName
        binding.questionTV.text = question.Question
        binding.optionABtn.text = question.Answer_1
        binding.optionBBtn.text = question.Answer_2
        binding.optionCBtn.text = question.Answer_3
        binding.optionDBtn.text = question.Answer_4
        binding.questionResultTV.visibility = View.INVISIBLE

        binding.optionABtn.setOnClickListener{
            if (binding.optionABtn.text.toString() == question.CorrectAnswer) {
                displayResult(true)
                checkCorrectAnswer(binding.optionABtn.text.toString())
            } else {
                displayResult(false)
                checkCorrectAnswer(binding.optionABtn.text.toString())
                binding.optionABtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.red))
            }

            countDownTimer.cancel()
            afterClick()
        }
        binding.optionBBtn.setOnClickListener{
            if (binding.optionBBtn.text.toString() == question.CorrectAnswer) {
                displayResult(true)
                checkCorrectAnswer(binding.optionBBtn.text.toString())
            } else {
                displayResult(false)
                checkCorrectAnswer(binding.optionBBtn.text.toString())
                binding.optionBBtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.red))
            }
            countDownTimer.cancel()
            afterClick()
        }
        binding.optionCBtn.setOnClickListener{
            if (binding.optionCBtn.text.toString() == question.CorrectAnswer) {
                displayResult(true)
                checkCorrectAnswer(binding.optionCBtn.text.toString())
            } else {
                displayResult(false)
                checkCorrectAnswer(binding.optionCBtn.text.toString())
                binding.optionCBtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.red))
            }
            countDownTimer.cancel()
            afterClick()
        }
        binding.optionDBtn.setOnClickListener{
            if (binding.optionDBtn.text.toString() == question.CorrectAnswer) {
                displayResult(true)
                checkCorrectAnswer(binding.optionDBtn.text.toString())
            } else {
                displayResult(false)
                checkCorrectAnswer(binding.optionDBtn.text.toString())
                binding.optionDBtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.red))
            }
            countDownTimer.cancel()
            afterClick()
        }
        startCountDownTimer()
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished / 1000).toInt()
                binding.secondsProgressBar.progress = progress
                binding.progressSecondsTV.text = progress.toString() // Show remaining seconds
            }

            override fun onFinish() {
                binding.secondsProgressBar.progress = 0
                displayResult(false)
                afterClick()
            }
        }

    }

    fun afterClick() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            resetButtonColors()
            startQuestion(questions[currentQuestionIndex])
        } else {
            val intent = Intent(this, ScoreDisplayActivity::class.java).apply {
                putExtra("score", score)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun resetButtonColors() {
        // Reset button colors to default (so the green/red doesn't persist across questions)
        binding.optionABtn.setBackgroundColor(ContextCompat.getColor(this, R.color.lavender))
        binding.optionBBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.lavender))
        binding.optionCBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.lavender))
        binding.optionDBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.lavender))
    }


    fun checkCorrectAnswer(correctAnswer: String) {
        when (correctAnswer) {
            binding.optionABtn.text -> {
                binding.optionABtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.green))
                score++
            }
            binding.optionBBtn.text -> {
                binding.optionBBtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.green))
                score++
            }
            binding.optionCBtn.text -> {
                binding.optionCBtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.green))
                score++
            }
            binding.optionDBtn.text -> {
                binding.optionDBtn.setBackgroundColor(ContextCompat.getColor(this@QuizActivity, R.color.green))
                score++
            }
        }
    }


    fun displayResult(result: Boolean){
        if(result == false)
        {
            binding.questionResultTV.visibility = View.VISIBLE
            binding.questionResultTV.text = "Wrong!"
            binding.questionResultTV.setTextColor(ContextCompat.getColor(this@QuizActivity,R.color.red))

        }
        if(result == true){
            binding.questionResultTV.visibility = View.VISIBLE
            binding.questionResultTV.text = "Correct!"
            binding.questionResultTV.setTextColor(ContextCompat.getColor(this@QuizActivity,R.color.green))

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

}