package com.example.opsc_quizcore

import android.content.Intent
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
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val userID = auth.uid

        db.collection("Users").whereEqualTo("ID", userID).limit(1).get()
            .addOnSuccessListener { userSnapshot ->
                val userDocument = userSnapshot.documents[0]
                user = userDocument.toObject(UserModel::class.java)
            }
        if (user?.Image != null) {
            val profileImage = user?.Image.toString()
            val imageUri = Uri.parse(profileImage)
            binding.userProfileImage.setImageURI(imageUri)
        }

        binding.userTV.text = user?.Username.toString()
        binding.scoreTv.text = user?.Score.toString()

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
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}