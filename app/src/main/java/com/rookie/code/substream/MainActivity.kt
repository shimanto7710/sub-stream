package com.rookie.code.substream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.rookie.code.substream.presentation.screen.HomeScreen
import com.rookie.code.substream.presentation.screen.VideoScreen
import com.rookie.code.substream.ui.theme.SubStreamTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SubStreamTheme {
                var currentScreen by remember { mutableStateOf("home") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = { Text("SubStream") },
                            actions = {
                                TextButton(
                                    onClick = {
                                        println("MainActivity: Button clicked, current screen: $currentScreen")
                                        currentScreen = if (currentScreen == "home") "videos" else "home"
                                        println("MainActivity: New screen: $currentScreen")
                                    }
                                ) {
                                    Text(
                                        text = if (currentScreen == "home") "Videos" else "Home",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> {
                            println("MainActivity: Showing HomeScreen")
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "videos" -> {
                            println("MainActivity: Showing VideoScreen")
                            VideoScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SubStreamTheme {
        Greeting("Android")
    }
}