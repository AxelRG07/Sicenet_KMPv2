package com.example.sicenet_kmpv2

import com.example.sicenet_kmpv2.network.sicenetHttpClient
import com.example.sicenet_kmpv2.data.repository.SicenetRepository

class AppContainer {
    val repository = SicenetRepository(sicenetHttpClient)
}