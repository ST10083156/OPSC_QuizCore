package com.example.opsc_quizcore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_quizcore.Models.UserModel
import com.example.opsc_quizcore.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

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

        val userID = auth.uid.toString()

        db.collection("Users").whereEqualTo("id", userID).limit(1).get()
            .addOnSuccessListener { userSnapshot ->
                val userDocument = userSnapshot.documents[0]
                user = userDocument.toObject<UserModel>()
                updateUI()
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
    private fun updateUI() {
        if (user != null) {
            if (user?.Image != null) {
                loadImageFromUri(user?.Image.toString(), binding.userProfileImage)
            }

            binding.userTV.text = user?.Username.toString()
            binding.scoreTv.text = user?.Score.toString()
        }
    }
    private fun loadImageFromUri(imageUri: String, imageView: ImageView) {
        val uri = Uri.parse(imageUri)

        Picasso.get()
            .load(uri)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(imageView)
    }

}