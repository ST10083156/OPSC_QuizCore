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
import com.example.opsc_quizcore.Models.QuestionModel
import com.example.opsc_quizcore.Models.QuizModel
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
binding.addQuizBtn.setOnClickListener{
    val questionList : MutableList<QuestionModel> = mutableListOf()
    val question1 = QuestionModel(
        Question = "What is the capital of France?",
        Answer_1 = "Madrid",
        Answer_2 = "Rome",
        Answer_3 = "Paris",
        Answer_4 = "Berlin",
        CorrectAnswer = "Paris"
    )
    val question2 = QuestionModel(
        Question = "Which continent is the Sahara Desert located on?",
        Answer_1 = "Asia",
        Answer_2 = "Africa",
        Answer_3 = "Australia",
        Answer_4 = "South America",
        CorrectAnswer = "Africa"
    )
    val question3 = QuestionModel(
        Question = "Which ocean is the largest by area?",
        Answer_1 = "Atlantic Ocean",
        Answer_2 = "Indian Ocean",
        Answer_3 = "Arctic Ocean",
        Answer_4 = "Pacific Ocean",
        CorrectAnswer = "Pacific Ocean"
    )
    val question4 = QuestionModel(
        Question = "Which country is known as the Land of the Rising Sun?",
        Answer_1 = "China",
        Answer_2 = "Japan",
        Answer_3 = "South Korea",
        Answer_4 = "Thailand",
        CorrectAnswer = "Japan"
    )
    val question5 = QuestionModel(
        Question = "What is the longest river in the world?",
        Answer_1 = "Amazon River",
        Answer_2 = "Nile River",
        Answer_3 = "Mississippi River",
        Answer_4 = "Yangtze River",
        CorrectAnswer = "Nile River"
    )
    val question6 = QuestionModel(
        Question = "Mount Everest is located on which continent?",
        Answer_1 = "South America",
        Answer_2 = "Europe",
        Answer_3 = "Africa",
        Answer_4 = "Asia",
        CorrectAnswer = "Asia"
    )
    val question7 = QuestionModel(
        Question = "What is the capital of Australia?",
        Answer_1 = "Sydney",
        Answer_2 = "Canberra",
        Answer_3 = "Melbourne",
        Answer_4 = "Brisbane",
        CorrectAnswer = "Canberra"
    )
    val question8 = QuestionModel(
        Question = "Which country has the most people?",
        Answer_1 = "India",
        Answer_2 = "United States",
        Answer_3 = "China",
        Answer_4 = "Brazil",
        CorrectAnswer = "China"
    )
    val question9 = QuestionModel(
        Question = "What is the smallest country in the world?",
        Answer_1 = "Monaco",
        Answer_2 = "Vatican City",
        Answer_3 = "Lichtenstein",
        Answer_4 = "San Marino",
        CorrectAnswer = "Vatican City"
    )
    val question10 = QuestionModel(
        Question = "Which ocean lies to the east of the United States?",
        Answer_1 = "Pacific Ocean",
        Answer_2 = "Indian Ocean",
        Answer_3 = "Atlantic Ocean",
        Answer_4 = "Arctic Ocean",
        CorrectAnswer = "Pacific Ocean"
    )
    questionList.add(question1)
    questionList.add(question2)
    questionList.add(question3)
    questionList.add(question4)
    questionList.add(question5)
    questionList.add(question6)
    questionList.add(question7)
    questionList.add(question8)
    questionList.add(question9)
    questionList.add(question10)

    val quiz = QuizModel(
        Name = "Simple Geography",
        Category = "Geography",
        Questions = questionList.toList()

    )
    db.collection("Quizzes").add(quiz).addOnSuccessListener {
        Toast.makeText(this,"Successfully added quiz",Toast.LENGTH_SHORT)
    }
}
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

