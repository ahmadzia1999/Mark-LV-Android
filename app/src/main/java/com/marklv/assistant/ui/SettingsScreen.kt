package com.marklv.assistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marklv.assistant.ChatViewModel
import com.marklv.assistant.SettingsStore
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }

    var apiKey by remember { mutableStateOf(store.apiKey) }
    var assistantName by remember { mutableStateOf(store.assistantName) }
    var userName by remember { mutableStateOf(store.userName) }
    var voice by remember { mutableStateOf(store.voiceReplies) }
    var saved by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear the conversation?") },
            text = { Text("The assistant will forget everything said in this chat. Facts it saved stay saved.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearChat()
                    confirmClear = false
                    onBack()
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Gemini API key", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it; saved = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Paste your key here") },
                singleLine = true
            )
            Text(
                "Free key: open aistudio.google.com/apikey and press “Create API key”.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Names", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = assistantName,
                onValueChange = { assistantName = it; saved = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Assistant name (e.g. JARVIS)") },
                singleLine = true
            )
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it; saved = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your name") },
                singleLine = true
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Speak replies out loud", modifier = Modifier.weight(1f))
                Switch(checked = voice, onCheckedChange = { voice = it; saved = false })
            }

            Button(
                onClick = {
                    store.apiKey = apiKey
                    store.assistantName = assistantName
                    store.userName = userName
                    store.voiceReplies = voice
                    saved = true
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
            if (saved) {
                Text("Saved ✓", color = MaterialTheme.colorScheme.primary)
            }

            OutlinedButton(
                onClick = { confirmClear = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Clear conversation") }

            Text(
                "Mark LV for Android — voice assistant with phone skills: flashlight, battery, opening apps and sites, web search, time, and a memory that follows you.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
