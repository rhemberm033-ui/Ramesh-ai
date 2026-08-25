package com.rameshai.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rameshai.network.ApiClient
import com.rameshai.network.GeminiContent
import com.rameshai.network.GeminiPart
import com.rameshai.network.GeminiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Namaste Ramesh! Main aapka AI assistant hoon. Main aapki kya madad kar sakta hoon?", false)
    ),
    val isLoading: Boolean = false,
    val currentMode: String = "Chat"
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val apiKey = "AQ.Ab8RN6JsNBe2v4kbdroDYJ1YxJjqYxMpE5Sk1Us_uephsFwVUQ"

    fun setMode(mode: String) {
        _uiState.update { it.copy(currentMode = mode) }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(text = userText, isUser = true)
        _uiState.update { 
            it.copy(
                messages = it.messages + userMessage,
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val promptText = if (_uiState.value.currentMode != "Chat") {
                    "[Mode: ${_uiState.value.currentMode}] $userText"
                } else {
                    userText
                }

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = promptText))
                        )
                    )
                )

                val response = ApiClient.apiService.generateGeminiContent(apiKey, request)
                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Koi response prapt nahi hua."

                _uiState.update { 
                    it.copy(
                        messages = it.messages + ChatMessage(text = replyText, isUser = false),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        messages = it.messages + ChatMessage(text = "Error: ${e.localizedMessage ?: "Unknown error"}", isUser = false),
                        isLoading = false
                    )
                }
            }
        }
    }
}
