package com.example.sicenet_kmpv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sicenet_kmpv2.data.repository.SicenetRepository
import com.example.sicenet_kmpv2.network.sicenetHttpClient
import com.example.sicenet_kmpv2.ui.screens.SicenetApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            SicenetApp(
                repository = SicenetRepository(sicenetHttpClient)
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}