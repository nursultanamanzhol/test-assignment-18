package kz.jetpack.test_assigment_18_08_2024.network

import kz.jetpack.test_assigment_18_08_2024.data.AuthResponse
import kz.jetpack.test_assigment_18_08_2024.data.RegistrationData
import kz.jetpack.test_assigment_18_08_2024.data.UserProfile
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @POST("api/v1/users/send-auth-code/")
    suspend fun sendAuthCode(@Body requestBody: Map<String, String>)

    @POST("api/v1/users/check-auth-code/")
    suspend fun checkAuthCode(@Body requestBody: Map<String, String>): AuthResponse

    @POST("api/v1/users/register/")
    suspend fun registerUser(@Body registrationData: RegistrationData)

    @GET("api/v1/users/me/")
    suspend fun getUserProfile(): UserProfile

    @PUT("api/v1/users/me/")
    suspend fun updateUserProfile(@Body userProfile: UserProfile)

    // Добавьте другие запросы по мере необходимости
}


object RetrofitInstance {

    private const val BASE_URL = "https://plannerok.ru/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level =
            HttpLoggingInterceptor.Level.BODY // Уровень логирования - BODY для полного логирования запросов и ответов
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Добавляем логирующий интерцептор
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Присваиваем кастомный OkHttpClient с логированием
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}