package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_quizcore.databinding.ActivityDashboardBinding
import com.example.opsc_quizcore.databinding.ActivityScoreDisplayBinding
import com.example.opsc_quizcore.databinding.ActivitySelectQuizBinding
import com.google.firebase.auth.FirebaseAuth

class ScoreDisplayActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScoreDisplayBinding
    private lateinit var auth : FirebaseAuth
    private  var score : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityScoreDisplayBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score_display)

        applySavedTheme()

        score = intent.getIntExtra("score",0)

        binding.scoreTV.text=score.toString()
        resultResponseMessage(score)
        binding.quizzesBtn.setOnClickListener{
            val intent = Intent(this,SelectQuizActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.dashboardBtn.setOnClickListener{
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun resultResponseMessage(score : Int){
        if(3>score&& score >=0){
            binding.resultResponseTV.text = "Tough Luck!"
            binding.resultResponseTV.setTextColor(ContextCompat.getColor(this,R.color.red))
        }
        if(score>2&&score<6){
            binding.resultResponseTV.text = "You can do better!"
            binding.resultResponseTV.setTextColor(ContextCompat.getColor(this,R.color.orange))
        }
        if(score>5&&score<9){
            binding.resultResponseTV.text = "Not too bad!"
            binding.resultResponseTV.setTextColor(ContextCompat.getColor(this,R.color.yellow))
        }
        if(score>8){
            binding.resultResponseTV.text = "Excellent work!!"
            binding.resultResponseTV.setTextColor(ContextCompat.getColor(this,R.color.green))
        }

    }

    private fun applySavedTheme() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedTheme: String? = sharedPreferences.getString("theme", "Red")

        when (savedTheme) {
            "White" -> window.decorView.setBackgroundColor(Color.WHITE)
            "Blue" -> window.decorView.setBackgroundColor(Color.BLUE)
            "Green" -> window.decorView.setBackgroundColor(Color.GREEN)
        }
    }
}