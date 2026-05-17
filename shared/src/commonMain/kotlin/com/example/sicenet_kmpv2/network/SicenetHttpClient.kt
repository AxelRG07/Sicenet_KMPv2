package com.example.sicenet_kmpv2.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies

val sicenetHttpClient = HttpClient {
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
}