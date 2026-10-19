package com.marklv.assistant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiException(message: String) : Exception(message)

/**
 * Talks to the Gemini REST API using the same model ladder as the desktop
 * Mark LV: a rung that is out of quota or down is skipped for a while and
 * the next model answers instead.
 */
class GeminiClient(private val apiKey: String) {

    fun interface ToolExecutor {
        fun execute(name: String, args: JSONObject): String
    }

    private class SkipModelException(message: String) : Exception(message)

    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val cooldown = HashMap<String, Long>()

    suspend fun chat(
        contents: JSONArray,
        systemPrompt: String,
        tools: JSONArray,
        toolExecutor: ToolExecutor
    ): String = withContext(Dispatchers.IO) {
        var lastError = "Every model is unavailable right now. Try again in a few minutes."
        for (model in MODELS) {
            val until = cooldown[model] ?: 0L
            if (System.currentTimeMillis() < until) continue
            try {
                return@withContext runToolLoop(model, contents, systemPrompt, tools, toolExecutor)
            } catch (e: SkipModelException) {
                cooldown[model] = System.currentTimeMillis() + COOLDOWN_MS
                lastError = e.message ?: lastError
            }
        }
        throw GeminiException(lastError)
    }

    private fun runToolLoop(
        model: String,
        contents: JSONArray,
        systemPrompt: String,
        tools: JSONArray,
        toolExecutor: ToolExecutor
    ): String {
        val working = JSONArray(contents.toString())
        var round = 0
        while (true) {
            val body = JSONObject().apply {
                put(
                    "system_instruction",
                    JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                )
                put("contents", working)
                put("tools", JSONArray().put(JSONObject().put("function_declarations", tools)))
                put(
                    "generationConfig",
                    JSONObject().put("temperature", 0.6).put("maxOutputTokens", 2048)
                )
            }

            val response = post("models/$model:generateContent", body)

            val block = response.optJSONObject("promptFeedback")
                ?.optString("blockReason").orEmpty()
            if (block.isNotBlank()) {
                throw GeminiException("The reply was blocked ($block). Try rephrasing.")
            }

            val candidate = response.optJSONArray("candidates")?.optJSONObject(0)
                ?: throw SkipModelException("Model returned no candidates")
            val parts = candidate.optJSONObject("content")?.optJSONArray("parts") ?: JSONArray()

            val text = StringBuilder()
            val calls = ArrayList<Pair<String, JSONObject>>()
            for (i in 0 until parts.length()) {
                val p = parts.optJSONObject(i) ?: continue
                if (p.has("text")) text.append(p.getString("text"))
                val fc = p.optJSONObject("functionCall")
                if (fc != null) {
                    calls.add(Pair(fc.getString("name"), fc.optJSONObject("args") ?: JSONObject()))
                }
            }

            if (calls.isEmpty()) {
                if (text.isBlank()) {
                    if (candidate.optString("finishReason") == "SAFETY") {
                        throw GeminiException("The reply was blocked for safety. Try rephrasing.")
                    }
                    throw SkipModelException("Model returned an empty reply")
                }
                return text.toString()
            }

            working.put(JSONObject().put("role", "model").put("parts", parts))
            val responses = JSONArray()
            for ((name, args) in calls) {
                val result = try {
                    toolExecutor.execute(name, args)
                } catch (e: Exception) {
                    "Skill failed: ${e.message}"
                }
                responses.put(
                    JSONObject().put(
                        "functionResponse",
                        JSONObject()
                            .put("name", name)
                            .put("response", JSONObject().put("result", result))
                    )
                )
            }
            working.put(JSONObject().put("role", "user").put("parts", responses))

            round++
            if (round >= MAX_TOOL_ROUNDS) {
                return text.ifBlank { "Done." }.toString()
            }
        }
    }

    private fun post(path: String, body: JSONObject): JSONObject {
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/$path")
            .header("x-goog-api-key", apiKey)
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        http.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            val json = try {
                JSONObject(text)
            } catch (e: Exception) {
                JSONObject()
            }
            if (!response.isSuccessful) {
                val message = json.optJSONObject("error")?.optString("message").orEmpty()
                val code = response.code
                val skip = code == 429 || code == 404 || code >= 500 ||
                        message.contains("not found", ignoreCase = true) ||
                        message.contains("not supported", ignoreCase = true) ||
                        message.contains("quota", ignoreCase = true)
                if (skip) {
                    throw SkipModelException(message.ifBlank { "HTTP $code" })
                }
                throw GeminiException(message.ifBlank { "HTTP $code" })
            }
            return json
        }
    }

    companion object {
        // Measured order from the desktop Mark LV ladder. A rung that is out of
        // quota or unhealthy is skipped for ten minutes and the next answers.
        private val MODELS = listOf(
            "gemini-2.5-flash-lite",
            "gemini-3.5-flash-lite",
            "gemini-3.1-flash-lite",
            "gemini-flash-lite-latest",
            "gemini-2.5-flash",
            "gemini-3.5-flash",
            "gemini-3.6-flash",
            "gemini-3-flash-preview"
        )
        private const val COOLDOWN_MS = 10 * 60 * 1000L
        private const val MAX_TOOL_ROUNDS = 6
    }
}
