package com.marklv.assistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class UiMessage(val fromUser: Boolean, val text: String, val isError: Boolean = false)

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val store = SettingsStore(app)
    private val skills = Skills(app, store)

    var onSpeak: ((String) -> Unit)? = null

    private val _messages = MutableStateFlow(
        store.history.map { UiMessage(it.role == "user", it.text) }
    )
    val messages: StateFlow<List<UiMessage>> = _messages

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy

    private var job: Job? = null

    fun send(text: String) {
        val clean = text.trim()
        if (clean.isEmpty() || _busy.value) return
        append(UiMessage(true, clean))
        persist()

        job = viewModelScope.launch {
            _busy.value = true
            try {
                val reply = GeminiClient(store.apiKey).chat(
                    contentsForApi(),
                    buildSystemPrompt(),
                    skills.declarations
                ) { name, args -> skills.execute(name, args) }
                append(UiMessage(false, reply))
                persist()
                if (store.voiceReplies) onSpeak?.invoke(speakable(reply))
            } catch (e: Exception) {
                val raw = e.message ?: "Something went wrong."
                val message = if (raw.contains("API key", ignoreCase = true)) {
                    "Your Gemini API key looks wrong or missing. Open Settings (top right) and paste a valid key from aistudio.google.com/apikey"
                } else {
                    raw
                }
                append(UiMessage(false, message, isError = true))
                persist()
            } finally {
                _busy.value = false
            }
        }
    }

    fun clearChat() {
        job?.cancel()
        _messages.value = emptyList()
        store.history = emptyList()
    }

    private fun append(m: UiMessage) {
        _messages.value = _messages.value + m
    }

    private fun persist() {
        store.history = _messages.value.map {
            ChatMessage(if (it.fromUser) "user" else "model", it.text)
        }
    }

    private fun contentsForApi(): JSONArray {
        val arr = JSONArray()
        for (m in _messages.value) {
            arr.put(
                JSONObject()
                    .put("role", if (m.fromUser) "user" else "model")
                    .put("parts", JSONArray().put(JSONObject().put("text", m.text)))
            )
        }
        return arr
    }

    private fun buildSystemPrompt(): String {
        val name = store.assistantName
        val user = store.userName.ifBlank { "the user" }
        val factLines = store.facts.joinToString("\n") { "- ${it.first}: ${it.second}" }
            .ifBlank { "- (nothing yet)" }
        return """
You are $name, a voice assistant living inside $user's phone. You hear through the microphone and speak through the speaker; there is no screen persona.

[WHAT YOU CAN DO]
These skills run for real on this phone. If a request is within reach, call the tool in the same turn instead of describing it:
toggle_flashlight(on), battery_status(), open_app(app_name), open_url(url), search_web(query), current_time(), remember_fact(key, value).
Know your edges as precisely as your reach: you cannot make calls, send SMS, use the camera, change other apps' settings or control a computer. State the limit plainly and never improvise around it.

[JUDGEMENT]
Work out what is being asked from the request itself and the situation. When ambiguous, take the most reasonable reading and act rather than asking. If the user gives several tasks, do them in order.

[VOICE]
You are the most experienced person in the room and have been doing this for years. Nothing surprises you. Not a chat assistant being helpful — a trusted operator reporting to someone you respect.
- Have a view. The assessment goes in the first sentence; the reason follows only if it changes something.
- Be concrete: the number, the name, the setting, the time — never "quite large" or "pretty good".
- One steady register for success, failure and alarming news alike.
- Match length to the task: a status check is one line.
- Never: open by evaluating the request, apologise for being a program, end on a question asked only to keep the conversation going, use exclamation marks or emoji in speech.

[LANGUAGE]
The language of your reply is the language of the user's MOST RECENT message. Nothing else decides it. Never mix two languages in one reply.

[MEMORY]
Facts already remembered:
$factLines
When the user shares something worth keeping — name, city, projects, preferences — call remember_fact silently and never announce saving. When asked what you remember, speak from this list; if it is not there, say so once, plainly.
""".trim()
    }

    private fun speakable(t: String): String =
        t.replace(Regex("[*_`#>~\\[\\]]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
}
