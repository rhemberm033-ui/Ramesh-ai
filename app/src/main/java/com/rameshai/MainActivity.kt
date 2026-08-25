package com.rameshai

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rameshai.ui.chat.ChatScreen
import com.rameshai.ui.chat.ChatViewModel
import com.rameshai.ui.chat.VoiceScreen
import com.rameshai.ui.settings.SettingsScreen
import com.rameshai.ui.settings.SettingsState
import com.rameshai.ui.theme.RameshAITheme

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val requestMicPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            // Friendly error surfaced in-app rather than a raw permission dialog result.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var settings by remember { mutableStateOf(SettingsState()) }

            RameshAITheme(darkTheme = settings.darkTheme) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "chat") {
                    composable("chat") {
                        ChatScreen(
                            viewModel = chatViewModel,
                            onOpenVoiceScreen = {
                                requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
                                navController.navigate("voice")
                            },
                            onOpenSettings = { navController.navigate("settings") },
                            onOpenHistory = { /* TODO: chat history sidebar/drawer */ }
                        )
                    }
                    composable("voice") {
                        VoiceScreen(viewModel = chatViewModel, onBack = { navController.popBackStack() })
                    }
                    composable("settings") {
                        SettingsScreen(
                            state = settings,
                            onStateChange = {
                                settings = it
                                chatViewModel.toggleWebSearch(it.webSearchEnabled)
                            },
                            onBack = { navController.popBackStack() },
                            onClearHistory = { chatViewModel.clearConversation() }
                        )
                    }
                }
            }
        }
    }
}
