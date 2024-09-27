package com.example.opsc_quizcore

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

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_quiz)
        val categories = listOf("Sports","Entertainment","Animals","Geography","My Custom Quizzes")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
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




}

   private fun updateQuizRecyclerView(category: String) {
       if(category.equals("My Custom Quizzes")){

           customQuizList = fetchCustomQuizzes(auth.uid.toString())
           customQuizAdapter = CustomQuizAdapter(customQuizList)
           binding.quizRecyclerView.adapter = customQuizAdapter

       }
       else{
           quizList = fetchQuizzesByCategory(category)
           quizAdapter = QuizAdapter(quizList)
           binding.quizRecyclerView.adapter = quizAdapter
       }

    }

    private fun fetchCustomQuizzes(id : String) : MutableList<CustomQuizModel>{
        RetrofitClient.instance.getMyQuizzes(id).enqueue(object : Callback<List<CustomQuizModel>>{
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

        })
        return customQuizList
    }

    private fun fetchQuizzesByCategory(category: String): MutableList<QuizModel> {
        db.collection("Quizzes").whereEqualTo("Category", category).get().addOnSuccessListener {
            categoryQuizzes ->
            if(categoryQuizzes!=null){
                for(quiz in categoryQuizzes){
                    val toBeAddedQuiz = quiz.toObject<QuizModel>()
                    quizList.add(toBeAddedQuiz)
                }
            }
        }.addOnFailureListener{

        }
        return quizList
    }


    class QuizAdapter(private val quizList : List<QuizModel>) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>(){
        private lateinit var quizItemBinding : QuizListItemBinding
        inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val quizTV : TextView = quizItemBinding.quizNameTV
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_list_item,parent,false)
            return QuizViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
            val quiz = quizList[position]
            holder.quizTV
        }

        override fun getItemCount(): Int {
            return quizList.size
        }


    }
    class CustomQuizAdapter(private val quizList : List<CustomQuizModel>) : RecyclerView.Adapter<CustomQuizAdapter.CustomQuizViewHolder>(){
        private lateinit var quizItemBinding : QuizListItemBinding
        inner class CustomQuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val quizTV : TextView = quizItemBinding.quizNameTV
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomQuizViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_list_item,parent,false)
            return CustomQuizViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomQuizViewHolder, position: Int) {
            val quiz = quizList[position]
            holder.quizTV
        }

        override fun getItemCount(): Int {
            return quizList.size
        }


    }
}
