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
import com.rookie.code.substream.navigation.AppNavigation
import com.rookie.code.substream.presentation.screen.HomeScreen
//import com.rookie.code.substream.presentation.screen.VideoScreen
import com.rookie.code.substream.presentation.screen.PostsScreen
import com.rookie.code.substream.presentation.viewmodel.PostsViewModel
import org.koin.androidx.compose.koinViewModel
import com.rookie.code.substream.ui.theme.SubStreamTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SubStreamTheme {
                AppNavigation()
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