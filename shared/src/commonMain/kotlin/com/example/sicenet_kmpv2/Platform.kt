package com.example.sicenet_kmpv2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform