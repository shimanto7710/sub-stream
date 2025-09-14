package com.rookie.code.substream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.rookie.code.substream.di.initKoin
import com.rookie.code.substream.presentation.screen.HomeScreen
import com.rookie.code.substream.ui.theme.SubStreamTheme
import com.rookie.code.substream.data.ktor.NetworkProfiler

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Koin
        initKoin(this)
        
        enableEdgeToEdge()
        setContent {
            SubStreamTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = { Text("SubStream") },
                            actions = {
                                val context = LocalContext.current
                                TextButton(
                                    onClick = { NetworkProfiler.openProfiler(context) }
                                ) {
                                    Text("Profiler")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
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