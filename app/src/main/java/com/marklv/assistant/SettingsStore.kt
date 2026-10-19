package com.marklv.assistant

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class ChatMessage(val role: String, val text: String)

class SettingsStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("marklv", Context.MODE_PRIVATE)

    var apiKey: String
        get() = prefs.getString("gemini_api_key", "") ?: ""
        set(value) = prefs.edit().putString("gemini_api_key", value.trim()).apply()

    var assistantName: String
        get() = (prefs.getString("assistant_name", "JARVIS") ?: "JARVIS").ifBlank { "JARVIS" }
        set(value) = prefs.edit().putString("assistant_name", value.trim().ifBlank { "JARVIS" }).apply()

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(value) = prefs.edit().putString("user_name", value.trim()).apply()

    var voiceReplies: Boolean
        get() = prefs.getBoolean("voice_replies", true)
        set(value) = prefs.edit().putBoolean("voice_replies", value).apply()

    var facts: List<Pair<String, String>>
        get() {
            return try {
                val arr = JSONArray(prefs.getString("facts", "[]") ?: "[]")
                (0 until arr.length()).mapNotNull { i ->
                    val o = arr.optJSONObject(i) ?: return@mapNotNull null
                    Pair(o.optString("key"), o.optString("value"))
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            val arr = JSONArray()
            for ((k, v) in value) {
                arr.put(JSONObject().put("key", k).put("value", v))
            }
            prefs.edit().putString("facts", arr.toString()).apply()
        }

    fun addFact(key: String, value: String) {
        if (key.isBlank() || value.isBlank()) return
        val current = facts.filter { it.first != key }
        facts = current + Pair(key.trim(), value.trim())
    }

    var history: List<ChatMessage>
        get() {
            return try {
                val arr = JSONArray(prefs.getString("history", "[]") ?: "[]")
                (0 until arr.length()).mapNotNull { i ->
                    val o = arr.optJSONObject(i) ?: return@mapNotNull null
                    ChatMessage(o.optString("role"), o.optString("text"))
                }.filter { it.text.isNotBlank() }
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            val arr = JSONArray()
            for (m in value.takeLast(60)) {
                arr.put(JSONObject().put("role", m.role).put("text", m.text))
            }
            prefs.edit().putString("history", arr.toString()).apply()
        }
}
