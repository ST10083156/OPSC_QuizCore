package com.example.opsc_quizcore

import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_quizcore.ApiService.RetrofitClient
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.QuizModel
import com.example.opsc_quizcore.databinding.ActivitySelectQuizBinding
import com.example.opsc_quizcore.databinding.QuizListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import org.w3c.dom.Text
import retrofit2.Callback
import retrofit2.Response

class SelectQuizActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySelectQuizBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var quizList : MutableList<QuizModel>
    private lateinit var customQuizList : MutableList<CustomQuizModel>
    private lateinit var quizAdapter: QuizAdapter
    private lateinit var customQuizAdapter : CustomQuizAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectQuizBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        quizList = mutableListOf()
        customQuizList = mutableListOf()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val categories = listOf("Sports","Entertainment","Animals","Geography","My Custom Quizzes")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySelectSpinner.adapter=adapter

        binding.quizRecyclerView.layoutManager=LinearLayoutManager(this)

        binding.categorySelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                updateQuizRecyclerView(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        binding.backBtn.setOnClickListener{
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity( intent)
            finish()
        }

}

    private fun updateQuizRecyclerView(category: String) {
        if (category == "My Custom Quizzes") {
            fetchCustomQuizzes(auth.uid.toString(), object : CustomQuizFetchCallback {
                override fun onQuizzesFetched(quizzes: MutableList<CustomQuizModel>) {
                    customQuizList.clear()
                    customQuizList.addAll(quizzes)
                    if (::customQuizAdapter.isInitialized) {
                        customQuizAdapter.notifyDataSetChanged()
                    } else {
                        customQuizAdapter = CustomQuizAdapter(customQuizList)
                        binding.quizRecyclerView.adapter = customQuizAdapter
                    }
                }
            })
        } else {
            fetchQuizzesByCategory(category)
            if(::quizAdapter.isInitialized){
                binding.quizRecyclerView.adapter=quizAdapter
                quizAdapter.notifyDataSetChanged()
            }
            else{
                quizAdapter= QuizAdapter(quizList)
                binding.quizRecyclerView.adapter=quizAdapter
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



    /* RetrofitClient.instance.getMyQuizzes(id).enqueue(object : Callback<List<CustomQuizModel>>{
        override fun onResponse(
            call: retrofit2.Call<List<CustomQuizModel>>,
            response: Response<List<CustomQuizModel>>
        ) {
            if(response.isSuccessful){
                customQuizList = response.body()?.toMutableList()!!
            }
        }

        override fun onFailure(call: retrofit2.Call<List<CustomQuizModel>>, t: Throwable) {
            Toast.makeText(this@SelectQuizActivity,"Error : ${t.message}",Toast.LENGTH_SHORT).show()
        }

    })*/


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
                        quizAdapter = QuizAdapter(quizList)
                        binding.quizRecyclerView.adapter = quizAdapter
                    } else {
                        quizAdapter?.notifyDataSetChanged() // Notify the adapter about data changes
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@SelectQuizActivity, "Error fetching quizzes", Toast.LENGTH_SHORT).show()
            }
    }




    class QuizAdapter(private val quizList : List<QuizModel>) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>(){
        private lateinit var quizItemBinding : QuizListItemBinding


        inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val quizTV : TextView = quizItemBinding.quizNameTV

            init {
                itemView.setOnClickListener {
                    val quiz = quizList[adapterPosition]

                    val intent = Intent(itemView.context,QuizActivity::class.java).apply{
                        putExtra("questions",ArrayList(quiz.Questions))
                        putExtra("quizName",quiz.Name)
                    }
                    itemView.context.startActivity(intent)
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_list_item,parent,false)
            return QuizViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
            val quiz = quizList[position]
            holder.quizTV.text = quiz.Name
        }

        override fun getItemCount(): Int {
            return quizList.size
        }


    }
    class CustomQuizAdapter(private val quizList: List<CustomQuizModel>) : RecyclerView.Adapter<CustomQuizAdapter.CustomQuizViewHolder>() {

        inner class CustomQuizViewHolder(private val binding: QuizListItemBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    val quiz = quizList[adapterPosition]
                    val intent = Intent(itemView.context, QuizActivity::class.java).apply {
                        putExtra("questions", ArrayList(quiz.Questions))
                        putExtra("quizName", quiz.QuizName)
                    }
                    itemView.context.startActivity(intent)
                }
            }

            fun bind(quiz: CustomQuizModel) {
                binding.quizNameTV.text = quiz.QuizName
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomQuizViewHolder {
            val binding = QuizListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomQuizViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CustomQuizViewHolder, position: Int) {
            val quiz = quizList[position]
            holder.bind(quiz)
        }

        override fun getItemCount(): Int {
            return quizList.size
        }
    }




    interface CustomQuizFetchCallback {
        fun onQuizzesFetched(quizzes: MutableList<CustomQuizModel>)
    }

}

