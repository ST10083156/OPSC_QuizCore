package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_quizcore.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        applySavedTheme()

        // Google sign-in setup
        val googleSO = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSO)

        // Login button click listener
        binding.loginBtn.setOnClickListener {
            if (!binding.emailET.text.toString().isEmpty() && !binding.passwordET.text.toString().isEmpty()) {
                login()
            }
            else
            {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Register button click listener
        binding.regBtn.setOnClickListener {
            if (!binding.emailET.text.toString().isEmpty() && !binding.passwordET.text.toString().isEmpty()) {
                register()
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Google Sign-In button click listener
        binding.googleBtn.setOnClickListener {
            googleSSO()
        }

        // Settings button click listener
        binding.settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun login(){
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful)
            {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                checkUser()
            }
            else
            {
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun register(){

        var email = binding.emailET.text.toString()
        var password = binding.passwordET.text.toString()

        //attempts to create new user in firebase auth using email and password
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){

            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                checkUser()


            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun googleSSO() {
        val googleSignInIntent = googleSignInClient.signInIntent
        startActivityForResult(googleSignInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {

        try {
            val account = completedTask.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()


                       checkUser()
                    } else {
                        Toast.makeText(this, "Login unsuccessful", Toast.LENGTH_SHORT).show()

                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUser() {
        val userID = auth.uid
        db.collection("Users").whereEqualTo("id", userID).get().addOnSuccessListener { users ->
            if (users.isEmpty) {
                val intent = Intent(this, UserDetailsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}

