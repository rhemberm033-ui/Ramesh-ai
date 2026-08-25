package com.rameshai.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SettingsState(
    val assistantName: String = "Ramesh AI",
    val userName: String = "",
    val darkTheme: Boolean = true,
    val autoVoiceReply: Boolean = false,
    val saveChats: Boolean = true,
    val webSearchEnabled: Boolean = true,
    val memoryEnabled: Boolean = true,
    val speechRate: Float = 1.0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onStateChange: (SettingsState) -> Unit,
    onBack: () -> Unit,
    onClearHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = state.userName,
                onValueChange = { onStateChange(state.copy(userName = it)) },
                label = { Text("Your name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            SettingsSwitchRow("Dark theme", state.darkTheme) { onStateChange(state.copy(darkTheme = it)) }
            SettingsSwitchRow("Auto voice reply", state.autoVoiceReply) { onStateChange(state.copy(autoVoiceReply = it)) }
            SettingsSwitchRow("Save chat history", state.saveChats) { onStateChange(state.copy(saveChats = it)) }
            SettingsSwitchRow("Web search", state.webSearchEnabled) { onStateChange(state.copy(webSearchEnabled = it)) }
            SettingsSwitchRow("Memory", state.memoryEnabled) { onStateChange(state.copy(memoryEnabled = it)) }

            Spacer(Modifier.height(16.dp))
            Text("Speech speed: ${"%.1f".format(state.speechRate)}x")
            Slider(
                value = state.speechRate,
                onValueChange = { onStateChange(state.copy(speechRate = it)) },
                valueRange = 0.5f..2.0f
            )

            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onClearHistory) { Text("Clear all chat history") }
        }
    }
}

@Composable
private fun SettingsSwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
