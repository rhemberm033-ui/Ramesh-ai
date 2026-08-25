package com.rameshai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rameshai.model.ChatMessage
import com.rameshai.model.Sender
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageBubble(
    message: ChatMessage,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val isUser = message.sender == Sender.USER
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.surface
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(bubbleColor, RoundedCornerShape(18.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(message.text, color = textColor, style = MaterialTheme.typography.bodyLarge)

            if (message.usedWebSearch && message.sources.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "🔎 Searched the web · ${message.sources.size} source(s)",
                    color = textColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                color = textColor.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall
            )

            Row {
                IconButton(onClick = onCopy) { Icon(Icons.Default.ContentCopy, "Copy", tint = textColor.copy(alpha = 0.7f)) }
                if (!isUser) {
                    IconButton(onClick = onRegenerate) { Icon(Icons.Default.Refresh, "Regenerate", tint = textColor.copy(alpha = 0.7f)) }
                } else {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit", tint = textColor.copy(alpha = 0.7f)) }
                }
                IconButton(onClick = onShare) { Icon(Icons.Default.Share, "Share", tint = textColor.copy(alpha = 0.7f)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = textColor.copy(alpha = 0.7f)) }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Ramesh AI is thinking", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
