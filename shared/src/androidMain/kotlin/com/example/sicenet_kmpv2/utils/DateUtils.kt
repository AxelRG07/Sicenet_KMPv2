package com.example.sicenet_kmpv2.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatearFechaNativa(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}