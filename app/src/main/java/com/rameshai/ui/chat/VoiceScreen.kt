package com.rameshai.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rameshai.model.VoiceState
import com.rameshai.ui.components.AiOrb

@Composable
fun VoiceScreen(viewModel: ChatViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back to chat") }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AiOrb(state = state.voiceState, baseSize = 180)
            Spacer(Modifier.height(24.dp))
            Text(
                text = when (state.voiceState) {
                    VoiceState.IDLE -> "Tap to talk"
                    VoiceState.LISTENING -> "Listening…"
                    VoiceState.THINKING -> "Thinking…"
                    VoiceState.SPEAKING -> "Ramesh AI is speaking…"
                },
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (state.voiceState == VoiceState.SPEAKING) {
                Button(onClick = { viewModel.stopSpeaking() }) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Stop speaking")
                }
            } else {
                Button(onClick = { viewModel.startVoiceInput() }) {
                    Text(if (state.voiceState == VoiceState.LISTENING) "Listening…" else "Tap to talk")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) { Text("Switch to text chat") }
    }
}
