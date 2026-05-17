package com.example.sicenet_kmpv2

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.sicenet_kmpv2.data.repository.SicenetRepository
import com.example.sicenet_kmpv2.network.sicenetHttpClient
import com.example.sicenet_kmpv2.ui.screens.SicenetApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sicenet_KMPv2",
    ) {
        SicenetApp(SicenetRepository(sicenetHttpClient))
    }
}