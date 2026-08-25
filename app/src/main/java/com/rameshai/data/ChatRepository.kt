package com.rameshai.data

import com.rameshai.network.ApiClient
import com.rameshai.network.GeminiContent
import com.rameshai.network.GeminiPart
import com.rameshai.network.GeminiRequest

class ChatRepository {
    private val apiKey = "AQ.Ab8RN6JsNBe2v4kbdroDYJ1YxJjqYxMpE5Sk1Us_uephsFwVUQ"

    suspend fun sendMessage(message: String, mode: String = "Chat"): Result<String> {
        return try {
            val prompt = if (mode != "Chat") "[$mode] $message" else message
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            )
            val response = ApiClient.apiService.generateGeminiContent(apiKey, request)
            val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No response received"
            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
