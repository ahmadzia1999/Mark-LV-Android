package com.marklv.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.marklv.assistant.ui.ChatScreen
import com.marklv.assistant.ui.MarkLVTheme
import com.marklv.assistant.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarkLVTheme {
                var screen by remember { mutableStateOf("chat") }
                if (screen == "chat") {
                    ChatScreen(onOpenSettings = { screen = "settings" })
                } else {
                    SettingsScreen(onBack = { screen = "chat" })
                }
            }
        }
    }
}
