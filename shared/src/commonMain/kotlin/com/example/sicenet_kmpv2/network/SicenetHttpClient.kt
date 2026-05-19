package com.example.sicenet_kmpv2.network

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*

fun crearClienteHttp() = HttpClient {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
}