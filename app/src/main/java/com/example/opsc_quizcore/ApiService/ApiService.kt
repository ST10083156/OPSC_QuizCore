package com.example.opsc_quizcore.ApiService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.example.opsc_quizcore.Models.ApiResponse
import com.example.opsc_quizcore.Models.CustomQuizModel
import com.example.opsc_quizcore.Models.SettingsModel
import com.example.opsc_quizcore.Models.UserModel


interface ApiService {

    @POST("CustomQuiz/CreateQuiz")
    fun createQuiz(@Body customQuiz: CustomQuizModel): retrofit2.Call<ApiResponse>

    @GET("CustomQuiz/GetMyQuizzes/{id}")
    fun getMyQuizzes(@Query("uid") uid: String): retrofit2.Call<List<CustomQuizModel>>

    @POST("user/add-user")
    fun addUser(@Body user: UserModel): retrofit2.Call<ApiResponse>

    @POST("user/update-settings")
    fun updateSettings(@Body settings: SettingsModel, @Query("uid") uid: String): retrofit2.Call<ApiResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://10.0.2.2:7266/api/"

    private fun getOkHttpClient(): OkHttpClient {
        val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        val sslContext: SSLContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
