package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_quizcore.Models.UserModel
import com.example.opsc_quizcore.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private var user: UserModel? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        applySavedTheme()

        val userID = auth.uid

        db.collection("Users").whereEqualTo("ID", userID).limit(1).get()
            .addOnSuccessListener { userSnapshot ->

                if (userSnapshot.documents.isNotEmpty()) {
                    val userDocument = userSnapshot.documents[0]
                    user = userDocument.toObject(UserModel::class.java)


                    if (user?.Image != null) {
                        val profileImage = user?.Image.toString()
                        val imageUri = Uri.parse(profileImage)
                        binding.userProfileImage.setImageURI(imageUri)
                    }

                    binding.userTV.text = user?.Username.toString()
                    binding.scoreTv.text = user?.Score.toString()
                } else {
                    binding.userTV.text = "User not found"
                    binding.scoreTv.text = "0"
                    binding.userProfileImage.setImageURI(null)
                }
            }
            .addOnFailureListener { e ->
                binding.userTV.text = "Error: ${e.message}"
                binding.scoreTv.text = "0"
                binding.userProfileImage.setImageURI(null)
            }

        binding.leaderboardBtn.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.createQuizBtn.setOnClickListener {
            val intent = Intent(this, CreateQuizActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.quizBtn.setOnClickListener {
            val intent = Intent(this, SelectQuizActivity::class.java)
            startActivity(intent)
            finish()
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
}
