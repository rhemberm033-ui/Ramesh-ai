package com.rameshai.network

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * All AI / web-search / image-model calls go through YOUR backend.
 * The app never talks to OpenAI/Anthropic/etc. directly and never
 * embeds a provider API key. The backend is responsible for:
 *   - holding provider credentials
 *   - deciding which tool(s) to invoke (AI orchestrator, section 18)
 *   - returning whether web search was actually performed + sources
 */
interface RameshApiService {

    @POST("v1/chat")
    suspend fun sendChat(@Body request: ChatRequest): ChatResponse

    @POST("v1/search")
    suspend fun webSearch(@Body request: SearchRequest): SearchResponse

    @POST("v1/vision")
    suspend fun analyzeImage(@Body request: VisionRequest): VisionResponse

    @POST("v1/document")
    suspend fun summarizeDocument(@Body request: DocumentRequest): DocumentResponse
}

data class ChatRequest(
    val conversationId: String,
    val mode: String,
    val message: String,
    val history: List<ChatTurn>,
    val allowWebSearch: Boolean,
    val language: String = "auto" // "hi", "en", or "auto" for Hinglish
)

data class ChatTurn(val role: String, val content: String)

data class ChatResponse(
    val reply: String,
    val usedWebSearch: Boolean,
    val sources: List<SourceDto> = emptyList(),
    val isCodeHeavy: Boolean = false
)

data class SourceDto(val title: String, val url: String, val snippet: String)

data class SearchRequest(val query: String)
data class SearchResponse(val results: List<SourceDto>)

data class VisionRequest(val imageBase64: String, val prompt: String)
data class VisionResponse(val description: String, val extractedText: String? = null)

data class DocumentRequest(val fileBase64: String, val mimeType: String, val question: String?)
data class DocumentResponse(val summary: String)
