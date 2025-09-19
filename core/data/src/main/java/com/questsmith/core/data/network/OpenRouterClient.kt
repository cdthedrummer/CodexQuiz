package com.questsmith.core.data.network

import com.questsmith.core.domain.model.QuizResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OpenRouterClient(
    private val client: OkHttpClient,
    private val json: Json,
    private val apiKeyProvider: () -> String,
    private val packageNameProvider: () -> String,
    private val model: String = DEFAULT_MODEL
) {
    suspend fun generateCoachSummary(result: QuizResult): String = withContext(Dispatchers.IO) {
        val key = apiKeyProvider()
        require(key.isNotBlank()) { "OpenRouter API key is missing" }

        val payload = json.encodeToString(RequestBody.serializer(), buildRequest(result))
        val request = Request.Builder()
            .url(BASE_URL)
            .post(payload.toRequestBody(CONTENT_TYPE))
            .header("Authorization", "Bearer $key")
            .header("HTTP-Referer", packageNameProvider())
            .header("X-Title", "QuestSmith")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("OpenRouter error: ${'$'}{response.code}")
            }
            val body = response.body?.string().orEmpty()
            val completion = json.decodeFromString(ResponseBody.serializer(), body)
            completion.choices.firstOrNull()?.message?.content?.joinToString(separator = "\n") { it.text }
                ?: ""
        }
    }

    private fun buildRequest(result: QuizResult): RequestBody = RequestBody(
        model = model,
        messages = listOf(
            Message(role = "system", content = listOf(Content(text = SYSTEM_PROMPT))),
            Message(
                role = "user",
                content = listOf(
                    Content(
                        text = buildString {
                            appendLine("Summarize this quiz outcome for the player.")
                            appendLine("Tally: ${'$'}{result.tally}")
                            appendLine("Selections: ${'$'}{result.responses}")
                        }
                    )
                )
            )
        )
    )

    companion object {
        private const val BASE_URL = "https://api.openrouter.ai/v1/chat/completions"
        private val CONTENT_TYPE = "application/json".toMediaType()
        private const val SYSTEM_PROMPT = "Craft a friendly 2-3 paragraph coach summary focusing on strengths, growth edges, and 3 actionable starters."
        private const val DEFAULT_MODEL = "openrouter/chatgpt-5"
    }
}

@Serializable
private data class RequestBody(
    val model: String,
    val messages: List<Message>
)

@Serializable
private data class Message(
    val role: String,
    val content: List<Content>
)

@Serializable
private data class Content(
    val type: String = "text",
    val text: String
)

@Serializable
private data class ResponseBody(
    val choices: List<Choice>
)

@Serializable
private data class Choice(
    val message: ChoiceMessage
)

@Serializable
private data class ChoiceMessage(
    val role: String,
    val content: List<ChoiceContent>
)

@Serializable
private data class ChoiceContent(
    val type: String,
    val text: String
)
