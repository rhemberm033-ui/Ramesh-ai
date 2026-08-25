package com.rameshai.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rameshai.model.AssistantMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onOpenVoiceScreen: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ramesh AI") },
                navigationIcon = {
                    IconButton(onClick = onOpenHistory) { Icon(Icons.Default.Menu, "History") }
                },
                actions = {
                    IconButton(onClick = { viewModel.newChat() }) { Icon(Icons.Default.Add, "New chat") }
                    IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, "Settings") }
                }
            )
        },
        bottomBar = {
            Column {
                ModeSelector(selected = state.mode, onSelect = viewModel::setMode)
                InputBar(
                    input = input,
                    onInputChange = { input = it },
                    onSend = {
                        viewModel.sendMessage(input)
                        input = ""
                    },
                    onMicClick = onOpenVoiceScreen
                )
            }
        }
    ) { padding ->
        if (!state.isOnline) {
            OfflineBanner(Modifier.padding(padding))
        }

        if (state.messages.isEmpty()) {
            EmptyState(
                mode = state.mode,
                onQuickAction = { text -> viewModel.sendMessage(text) },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(state.messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        onCopy = { /* clipboard */ },
                        onRegenerate = { viewModel.sendMessage(msg.text) },
                        onEdit = { input = msg.text },
                        onShare = { /* share intent */ },
                        onDelete = { viewModel.deleteMessage(msg.id) }
                    )
                }
                if (state.isThinking) {
                    item { TypingIndicator() }
                }
            }
        }
    }
}

@Composable
private fun ModeSelector(selected: AssistantMode, onSelect: (AssistantMode) -> Unit) {
    val modes = listOf(
        AssistantMode.CHAT to "💬 Chat",
        AssistantMode.CODING to "💻 Coding",
        AssistantMode.STUDY to "📚 Study",
        AssistantMode.CREATIVE to "🎨 Creative"
    )
    LazyRowModes(modes, selected, onSelect)
}

@Composable
private fun LazyRowModes(
    modes: List<Pair<AssistantMode, String>>,
    selected: AssistantMode,
    onSelect: (AssistantMode) -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(modes) { (mode, label) ->
            FilterChip(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun InputBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Message Ramesh AI…") },
            shape = MaterialTheme.shapes.extraLarge
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onMicClick) { Icon(Icons.Default.Mic, "Voice") }
        IconButton(onClick = onSend, enabled = input.isNotBlank()) {
            Icon(Icons.Default.Send, "Send")
        }
    }
}

@Composable
private fun EmptyState(
    mode: AssistantMode,
    onQuickAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Hi, I'm Ramesh AI 👋", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("How can I help you today?", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))

        val quickActions = listOf(
            "💬 Ask anything" to "Hi Ramesh, tell me something interesting.",
            "💻 Write code" to "Help me write some code.",
            "📚 Study" to "Help me study a topic step by step.",
            "🔎 Search the web" to "What's the latest news today?"
        )
        quickActions.forEach { (label, prompt) ->
            OutlinedButton(
                onClick = { onQuickAction(prompt) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) { Text(label) }
        }
    }
}

@Composable
private fun OfflineBanner(modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = modifier.fillMaxWidth()) {
        Text(
            "Offline mode — you can still view chat history. Web search and live answers need a connection.",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
