package com.rameshai.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rameshai.data.ChatRepository
import com.rameshai.model.*
import com.rameshai.network.ChatTurn
import com.rameshai.voice.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatUiState(
    val conversationId: String = UUID.randomUUID().toString(),
    val messages: List<ChatMessage> = emptyList(),
    val mode: AssistantMode = AssistantMode.CHAT,
    val isThinking: Boolean = false,
    val webSearchEnabled: Boolean = true,
    val voiceState: VoiceState = VoiceState.IDLE,
    val isOnline: Boolean = true,
    val autoSpeakReplies: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository()
    val voiceManager = VoiceManager(application)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        voiceManager.init()
        viewModelScope.launch {
            voiceManager.state.collect { vs ->
                _uiState.value = _uiState.value.copy(voiceState = vs)
            }
        }
    }

    fun setMode(mode: AssistantMode) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun toggleWebSearch(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(webSearchEnabled = enabled)
    }

    fun newChat() {
        _uiState.value = ChatUiState(
            webSearchEnabled = _uiState.value.webSearchEnabled,
            autoSpeakReplies = _uiState.value.autoSpeakReplies
        )
    }

    fun sendMessage(text: String, attachments: List<Attachment> = emptyList()) {
        if (text.isBlank() && attachments.isEmpty()) return
        val state = _uiState.value
        val userMsg = ChatMessage(
            chatId = state.conversationId,
            sender = Sender.USER,
            text = text,
            mode = state.mode,
            attachments = attachments
        )
        _uiState.value = state.copy(
            messages = state.messages + userMsg,
            isThinking = true
        )

        viewModelScope.launch {
            val history = _uiState.value.messages.takeLast(20).map {
                ChatTurn(if (it.sender == Sender.USER) "user" else "assistant", it.text)
            }
            val reply = repository.sendMessage(
                conversationId = state.conversationId,
                mode = state.mode,
                text = text,
                history = history,
                allowWebSearch = state.webSearchEnabled
            )
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + reply,
                isThinking = false
            )
            if (_uiState.value.autoSpeakReplies && reply.text.isNotBlank()) {
                voiceManager.speak(shortSpokenForm(reply))
            }
        }
    }

    /** Section rule: don't read whole code blocks aloud automatically. */
    private fun shortSpokenForm(message: ChatMessage): String {
        return if (message.isCodeHeavy) {
            "Code ready hai. Maine ise chat mein file-by-file diya hai."
        } else {
            message.text
        }
    }

    fun deleteMessage(id: String) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages.filterNot { it.id == id }
        )
    }

    fun clearConversation() {
        _uiState.value = _uiState.value.copy(messages = emptyList())
    }

    fun startVoiceInput(languageTag: String = "hi-IN") {
        voiceManager.interruptSpeaking()
        voiceManager.startListening(
            languageTag = languageTag,
            onResult = { text -> sendMessage(text) },
            onError = { /* surface via a Snackbar in the UI layer */ }
        )
    }

    fun stopSpeaking() = voiceManager.pauseSpeaking()

    override fun onCleared() {
        voiceManager.release()
        super.onCleared()
    }
}
