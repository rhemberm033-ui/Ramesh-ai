package com.rameshai.model

import java.util.UUID

enum class Sender { USER, ASSISTANT }

enum class AssistantMode { CHAT, CODING, STUDY, CREATIVE }

enum class MessageStatus { SENDING, SENT, ERROR }

data class Attachment(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String
)

/**
 * A single chat message. [usedWebSearch] and [sources] are only ever set
 * when a real backend web-search call actually happened — the UI must
 * never claim search was used otherwise.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: AssistantMode = AssistantMode.CHAT,
    val attachments: List<Attachment> = emptyList(),
    val usedWebSearch: Boolean = false,
    val sources: List<WebSource> = emptyList(),
    val status: MessageStatus = MessageStatus.SENT,
    val isCodeHeavy: Boolean = false
)

data class WebSource(
    val title: String,
    val url: String,
    val snippet: String
)

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val pinned: Boolean = false,
    val favorite: Boolean = false
)

enum class VoiceState { IDLE, LISTENING, THINKING, SPEAKING }
