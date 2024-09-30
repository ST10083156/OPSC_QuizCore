package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_quizcore.ApiService.RetrofitClient
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.QuizModel
import com.example.opsc_quizcore.databinding.ActivitySelectQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var quizList: MutableList<QuizModel> = mutableListOf()
    private var customQuizList: MutableList<CustomQuizModel> = mutableListOf()
    private var combinedQuizList: MutableList<Any> = mutableListOf()
    private lateinit var quizAdapter: CombinedQuizAdapter
    private lateinit var categorySpinner: Spinner
    private var categoriesList: MutableList<String> = mutableListOf("All")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectQuizBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        enableEdgeToEdge()
        setContentView(binding.root)

        applySavedTheme()

        binding.returnBtn.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


        binding.quizRecyclerView.layoutManager = LinearLayoutManager(this)
        categorySpinner = binding.categorySelectSpinner


        fetchCategories {
            fetchAllQuizzes()
        }
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = categoriesList[position]
                filterQuizzesByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun applySavedTheme() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val savedTheme: String? = sharedPreferences.getString("theme", "Red")

        window.decorView.setBackgroundColor(
            when (savedTheme) {
                "White" -> Color.WHITE
                "Blue" -> Color.BLUE
                "Green" -> Color.GREEN
                else -> Color.RED
            }
        )
    }

    private fun fetchCategories(onSuccess: () -> Unit) {
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val category = document.getString("Category") ?: "Unknown"
                    if (category !in categoriesList) {
                        categoriesList.add(category)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching categories: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAllQuizzes() {
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val quiz = document.toObject<QuizModel>()
                    quizList.add(quiz)
                }
                fetchCustomQuizzes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching quizzes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchCustomQuizzes() {
        db.collection("custom_quizzes")
            .whereEqualTo("UserID", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val customQuiz = document.toObject<CustomQuizModel>()
                    customQuizList.add(customQuiz)
                }
                combineQuizLists()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching custom quizzes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun combineQuizLists() {
        combinedQuizList.clear()
        combinedQuizList.addAll(quizList)
        combinedQuizList.addAll(customQuizList)


        quizAdapter = CombinedQuizAdapter(combinedQuizList)
        binding.quizRecyclerView.adapter = quizAdapter
    }

    private fun filterQuizzesByCategory(selectedCategory: String) {
        if (!::quizAdapter.isInitialized) {

            return
        }

        if (selectedCategory == "All") {
            quizAdapter.updateData(combinedQuizList)
            return
        }
        val filteredQuizzes = combinedQuizList.filter {
            when (it) {
                is QuizModel -> it.Category == selectedCategory
                is CustomQuizModel -> it.Category == selectedCategory
                else -> false
            }
        }
        quizAdapter.updateData(filteredQuizzes)
    }


    inner class CombinedQuizAdapter(private var quizList: List<Any>) : RecyclerView.Adapter<CombinedQuizAdapter.QuizViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
            val view = layoutInflater.inflate(R.layout.activity_quiz_item, parent, false)
            return QuizViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
            when (val item = quizList[position]) {
                is QuizModel -> {
                    holder.bindQuiz(item)
                }
                is CustomQuizModel -> {
                    holder.bindCustomQuiz(item)
                }
            }
        }

        override fun getItemCount(): Int = quizList.size

        fun updateData(newQuizList: List<Any>) {
            quizList = newQuizList
            notifyDataSetChanged()
        }

        inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val quizName: TextView = itemView.findViewById(R.id.quizNameTextView)
            private val quizCategory: TextView = itemView.findViewById(R.id.quizCategoryTextView)

            fun bindQuiz(quiz: QuizModel) {
                quizName.text = quiz.Name // Ensure this property exists in QuizModel
                quizCategory.text = quiz.Category
            }

            fun bindCustomQuiz(customQuiz: CustomQuizModel) {
                quizName.text = customQuiz.QuizName // Ensure QuizName exists in CustomQuizModel
                quizCategory.text = customQuiz.Category
            }
        }
    }
}
