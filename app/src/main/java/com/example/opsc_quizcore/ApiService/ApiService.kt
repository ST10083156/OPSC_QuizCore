package com.example.opsc_quizcore.ApiService
import com.example.opsc_quizcore.Models.ApiResponse
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.QuizModel
import com.example.opsc_quizcore.Models.SettingsModel
import com.example.opsc_quizcore.Models.UserModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {


    @POST("/CustomQuiz/CreateQuiz/{quiz}")
    fun CreateQuiz(@Body customQuiz: CustomQuizModel)

    @GET("CustomQuiz/GetMyQuizzes/{id}")
    fun getMyQuizzes(@Query("uid") uid: String): Call<List<CustomQuizModel>>

    @POST("api/user/add-user")
    fun addUser(@Body user: UserModel): Call<ApiResponse>

    @POST("api/user/update-settings")
    fun updateSettings(@Body settings: SettingsModel, @Query("uid") uid: String): Call<ApiResponse>



}

object RetrofitClient {
    private const val BASE_URL = "https://localhost:7266/api/" // Replace with your API base URL

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
            .build()
            .create(ApiService::class.java)
    }
}