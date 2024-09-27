package com.example.opsc_quizcore

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.example.opsc_quizcore.Models.UserModel
import com.example.opsc_quizcore.databinding.ActivityUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserDetailsActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserDetailsBinding
    private  var imageUri: Uri? = null
    private lateinit var user : UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.addImageBtn.setOnClickListener{
            pictureAdd()
        }

        binding.continueBtn.setOnClickListener{
            if(binding.nameET.text!=null|| binding.usernameET.text!=null)
            {
                if(binding.userImageView.drawable == R.drawable.ic_launcher_foreground.toDrawable())
                {
                    user = UserModel(
                        ID = auth.uid.toString(),
                        Name = binding.nameET.text.toString(),
                        Username = binding.usernameET.text.toString(),
                        Image = null,
                        Score = 0
                    )
                }
                else{
                    user = UserModel(
                        ID = auth.uid.toString(),
                        Name = binding.nameET.text.toString(),
                        Username = binding.usernameET.text.toString(),
                        Image = imageUri,
                        Score = 0
                    )
                }

            }

            db = FirebaseFirestore.getInstance()
            db.collection("Users").add(user)
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    fun pictureAdd(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            binding.userImageView.setImageURI(selectedImageUri)
            imageUri=selectedImageUri
        }
    }

}