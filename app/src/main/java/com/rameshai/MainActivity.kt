package com.rameshai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rameshai.ui.chat.ChatScreen
import com.rameshai.ui.chat.ChatViewModel
import com.rameshai.ui.theme.RameshAITheme

class MainActivity : ComponentActivity() {
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RameshAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "chat"
                    ) {
                        composable("chat") {
                            ChatScreen(
                                viewModel = chatViewModel,
                                onOpenVoiceScreen = { navController.navigate("voice") },
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenHistory = { navController.navigate("history") }
                            )
                        }
                        composable("voice") {
                        }
                        composable("settings") {
                        }
                        composable("history") {
                        }
                    }
                }
            }
        }
    }
}
