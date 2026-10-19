package com.marklv.assistant

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Skills the assistant can really perform on this phone — the Android
 * counterpart of the desktop app's actions/ folder. The Gemini model calls
 * these through function calling; the result is data for the model, which
 * then speaks it in the user's own language.
 */
class Skills(
    private val context: Context,
    private val store: SettingsStore
) {

    private val main = Handler(Looper.getMainLooper())

    val declarations: JSONArray = JSONArray(
        """
        [
          {"name": "toggle_flashlight",
           "description": "Turn the phone's flashlight on or off.",
           "parameters": {"type": "OBJECT",
             "properties": {"on": {"type": "BOOLEAN", "description": "true = on, false = off"}},
             "required": ["on"]}},
          {"name": "battery_status",
           "description": "Report the phone's battery percentage and whether it is charging.",
           "parameters": {"type": "OBJECT", "properties": {}}},
          {"name": "open_app",
           "description": "Open an installed app by its name, e.g. 'WhatsApp' or 'YouTube'.",
           "parameters": {"type": "OBJECT",
             "properties": {"app_name": {"type": "STRING", "description": "The app name"}},
             "required": ["app_name"]}},
          {"name": "open_url",
           "description": "Open a website in the phone's browser, e.g. 'youtube.com'.",
           "parameters": {"type": "OBJECT",
             "properties": {"url": {"type": "STRING", "description": "The site address"}},
             "required": ["url"]}},
          {"name": "search_web",
           "description": "Search the web in the browser for a phrase.",
           "parameters": {"type": "OBJECT",
             "properties": {"query": {"type": "STRING", "description": "What to search for"}},
             "required": ["query"]}},
          {"name": "current_time",
           "description": "Get the phone's current date and time.",
           "parameters": {"type": "OBJECT", "properties": {}}},
          {"name": "remember_fact",
           "description": "Silently store a fact about the user for future sessions (name, city, projects, preferences). Do not announce that you are saving.",
           "parameters": {"type": "OBJECT",
             "properties": {"key": {"type": "STRING", "description": "Short label, e.g. 'city' or 'job'"},
                            "value": {"type": "STRING", "description": "The fact itself"}},
             "required": ["key", "value"]}}
        ]
        """.trimIndent()
    )

    fun execute(name: String, args: JSONObject): String = when (name) {
        "toggle_flashlight" -> toggleFlashlight(args.optBoolean("on", true))
        "battery_status" -> batteryStatus()
        "open_app" -> openApp(args.optString("app_name", ""))
        "open_url" -> openUrl(args.optString("url", ""))
        "search_web" -> searchWeb(args.optString("query", ""))
        "current_time" -> currentTime()
        "remember_fact" -> rememberFact(args.optString("key", ""), args.optString("value", ""))
        else -> "Unknown skill: $name"
    }

    private fun toggleFlashlight(on: Boolean): String {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cm.cameraIdList.firstOrNull { id ->
            cm.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: return "This phone has no flashlight."
        return try {
            cm.setTorchMode(cameraId, on)
            if (on) "Flashlight is ON" else "Flashlight is OFF"
        } catch (e: Exception) {
            "Could not control the flashlight: ${e.message}"
        }
    }

    private fun batteryStatus(): String {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val percent = if (level >= 0) level * 100 / scale else -1
        val charging = plugged != 0
        return "Battery at $percent percent, ${if (charging) "charging" else "on battery"}."
    }

    private fun openApp(name: String): String {
        if (name.isBlank()) return "No app name given."
        val pm = context.packageManager
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(query, 0)
        val wanted = name.lowercase(Locale.ROOT)
        val target = apps.firstOrNull {
            it.loadLabel(pm).toString().lowercase(Locale.ROOT).contains(wanted)
        } ?: return "No installed app found matching '$name'."
        val label = target.loadLabel(pm).toString()
        val launch = pm.getLaunchIntentForPackage(target.activityInfo.packageName)
            ?: return "Could not open $label."
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        main.post { context.startActivity(launch) }
        return "Opened $label."
    }

    private fun openUrl(url: String): String {
        if (url.isBlank()) return "No address given."
        val fixed = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixed))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            main.post { context.startActivity(intent) }
            "Opened $fixed"
        } catch (e: Exception) {
            "Could not open $fixed: ${e.message}"
        }
    }

    private fun searchWeb(query: String): String {
        if (query.isBlank()) return "Nothing to search for."
        val url = "https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8")
        return openUrl(url)
    }

    private fun currentTime(): String {
        val format = SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm", Locale.getDefault())
        return "It is ${format.format(Date())}."
    }

    private fun rememberFact(key: String, value: String): String {
        store.addFact(key, value)
        return "Saved."
    }
}
