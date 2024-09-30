package com.example.opsc_quizcore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_quizcore.Models.UserModel
import com.example.opsc_quizcore.databinding.ActivityLeaderboardBinding
import com.example.opsc_quizcore.databinding.LeaderboardListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        val usersList = mutableListOf<UserModel>()


        db.collection("Users")
            .orderBy("Score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    documents.forEach { user ->
                        usersList.add(user.toObject(UserModel::class.java))
                    }
                }


                val adapter = LeaderboardAdapter(this, usersList)
                binding.userListView.adapter = adapter
            }


        binding.backBtn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

class LeaderboardAdapter(
    private val context: Context,
    private val userList: List<UserModel>
) : ArrayAdapter<UserModel>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemBinding: LeaderboardListItemBinding =
            if (convertView == null) {
                LeaderboardListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            } else {
                LeaderboardListItemBinding.bind(convertView)
            }

        val currentUser = getItem(position)

        if (currentUser?.Image == null) {
            listItemBinding.usernameTV.text = currentUser?.Username.toString()
            listItemBinding.leaderboardScoreTv.text = currentUser?.Score.toString()
        } else {
            listItemBinding.usernameTV.text = currentUser?.Username.toString()
            listItemBinding.leaderboardScoreTv.text = currentUser?.Score.toString()
            val imageString = currentUser?.Image.toString()
            val imageUri = Uri.parse(imageString)
            listItemBinding.userImage.setImageURI(imageUri)
        }

        return listItemBinding.root
    }
}
