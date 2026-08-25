package com.rameshai.data.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class GeminiPart(
    @SerializedName("text") val text: String
)

data class GeminiContent(
    @SerializedName("parts") val parts: List<GeminiPart>,
    @SerializedName("role") val role: String? = null
)

data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent?
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?
)

interface RameshApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateGeminiContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object ApiClient {
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: RameshApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RameshApiService::class.java)
    }
}
