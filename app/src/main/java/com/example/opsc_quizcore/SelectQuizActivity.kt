package com.example.opsc_quizcore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.QuizModel
import com.example.opsc_quizcore.databinding.ActivitySelectQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class SelectQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectQuizBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var quizList: MutableList<QuizModel>
    private lateinit var customQuizList: MutableList<CustomQuizModel>
    private lateinit var quizAdapter: QuizListAdapter
    private lateinit var customQuizAdapter: CustomQuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        quizList = mutableListOf()
        customQuizList = mutableListOf()

        enableEdgeToEdge()

        val categories = listOf("Sports", "Entertainment", "Animals", "Geography", "My Custom Quizzes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySelectSpinner.adapter = adapter

        binding.categorySelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                updateQuizListView(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateQuizListView(category: String) {
        if (category == "My Custom Quizzes") {
            fetchCustomQuizzes(auth.uid.toString(), object : CustomQuizFetchCallback {
                override fun onQuizzesFetched(quizzes: MutableList<CustomQuizModel>) {
                    customQuizList.clear()
                    customQuizList.addAll(quizzes)
                    if (::customQuizAdapter.isInitialized) {
                        customQuizAdapter.notifyDataSetChanged()
                    } else {
                        customQuizAdapter = CustomQuizListAdapter(customQuizList)
                        binding.quizListView.adapter = customQuizAdapter
                    }
                }
            })
        } else {
            fetchQuizzesByCategory(category)
            if (::quizAdapter.isInitialized) {
                binding.quizListView.adapter = quizAdapter
                quizAdapter.notifyDataSetChanged()
            } else {
                quizAdapter = QuizListAdapter(quizList)
                binding.quizListView.adapter = quizAdapter
                quizAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchCustomQuizzes(id: String, callback: CustomQuizFetchCallback) {
        db.collection("CustomQuizzes").whereEqualTo("userID", id).get().addOnSuccessListener { quizzes ->
            val customQuizList = mutableListOf<CustomQuizModel>()
            if (!quizzes.isEmpty) {
                for (quiz in quizzes) {
                    customQuizList.clear()
                    val customQuiz = quiz.toObject<CustomQuizModel>()
                    customQuizList.add(customQuiz)
                }
            }
            callback.onQuizzesFetched(customQuizList)
        }.addOnFailureListener { exception ->
            Toast.makeText(this@SelectQuizActivity, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchQuizzesByCategory(category: String) {
        db.collection("Quizzes").whereEqualTo("category", category).get()
            .addOnSuccessListener { categoryQuizzes ->
                if (categoryQuizzes != null) {
                    quizList.clear()
                    for (quiz in categoryQuizzes) {
                        val toBeAddedQuiz = quiz.toObject<QuizModel>()
                        quizList.add(toBeAddedQuiz)
                    }
                    if (quizAdapter == null) {
                        quizAdapter = QuizListAdapter(quizList)
                        binding.quizListView.adapter = quizAdapter
                    } else {
                        quizAdapter?.notifyDataSetChanged()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@SelectQuizActivity, "Error fetching quizzes", Toast.LENGTH_SHORT).show()
            }
    }

    class QuizListAdapter(private val quizList: List<QuizModel>) : BaseAdapter() {
        override fun getCount(): Int {
            return quizList.size
        }

        override fun getItem(position: Int): Any {
            return quizList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(parent.context)
            val view = convertView ?: inflater.inflate(R.layout.quiz_list_item, parent, false)
            val quiz = quizList[position]
            val quizNameTV: TextView = view.findViewById(R.id.quizNameTV)

            quizNameTV.text = quiz.Name

            view.setOnClickListener {
                val intent = Intent(parent.context, QuizActivity::class.java).apply {
                    putExtra("questions", ArrayList(quiz.Questions))
                    putExtra("quizName", quiz.Name)
                }
                parent.context.startActivity(intent)
            }

            return view
        }
    }

    class CustomQuizListAdapter(private val quizList: List<CustomQuizModel>) : BaseAdapter() {
        override fun getCount(): Int {
            return quizList.size
        }

        override fun getItem(position: Int): Any {
            return quizList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(parent.context)
            val view = convertView ?: inflater.inflate(R.layout.quiz_list_item, parent, false)
            val quiz = quizList[position]
            val quizNameTV: TextView = view.findViewById(R.id.quizNameTV)

            quizNameTV.text = quiz.QuizName

            view.setOnClickListener {
                val intent = Intent(parent.context, QuizActivity::class.java).apply {
                    putExtra("questions", ArrayList(quiz.Questions))
                    putExtra("quizName", quiz.QuizName)
                }
                parent.context.startActivity(intent)
            }

            return view
        }
    }

    interface CustomQuizFetchCallback {
        fun onQuizzesFetched(quizzes: MutableList<CustomQuizModel>)
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
