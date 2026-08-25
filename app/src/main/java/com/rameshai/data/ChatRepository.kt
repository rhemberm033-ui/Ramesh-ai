package com.rameshai.data

import com.rameshai.model.*
import com.rameshai.network.*

/**
 * Single choke point for every AI/search/vision/document call.
 *
 * Rule enforced here (per product spec, section 1/5/6):
 *   - We NEVER report usedWebSearch = true unless the backend actually
 *     returned that it performed a search.
 *   - When the app is running against the placeholder backend URL
 *     (NetworkModule.isDemoMode), we clearly label responses as demo
 *     output instead of silently pretending to be a fully working AI.
 */
class ChatRepository {

    suspend fun sendMessage(
        conversationId: String,
        mode: AssistantMode,
        text: String,
        history: List<ChatTurn>,
        allowWebSearch: Boolean
    ): ChatMessage {
        if (NetworkModule.isDemoMode) {
            return ChatMessage(
                chatId = conversationId,
                sender = Sender.ASSISTANT,
                text = "[Demo mode] No backend is configured yet, so this is a placeholder " +
                        "reply rather than a real AI response. Point BACKEND_BASE_URL at your " +
                        "server in app/build.gradle.kts to enable real answers.\n\nYou said: \"$text\"",
                mode = mode,
                usedWebSearch = false
            )
        }

        return try {
            val response = NetworkModule.api.sendChat(
                ChatRequest(
                    conversationId = conversationId,
                    mode = mode.name.lowercase(),
                    message = text,
                    history = history,
                    allowWebSearch = allowWebSearch
                )
            )
            ChatMessage(
                chatId = conversationId,
                sender = Sender.ASSISTANT,
                text = response.reply,
                mode = mode,
                usedWebSearch = response.usedWebSearch,
                sources = response.sources.map { WebSource(it.title, it.url, it.snippet) },
                isCodeHeavy = response.isCodeHeavy
            )
        } catch (e: Exception) {
            ChatMessage(
                chatId = conversationId,
                sender = Sender.ASSISTANT,
                text = "Something went wrong reaching the assistant. Please try again.",
                mode = mode,
                status = MessageStatus.ERROR
            )
        }
    }
}
