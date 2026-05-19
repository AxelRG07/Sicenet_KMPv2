package com.example.sicenet_kmpv2.domain

data class PerfilAcademico(
    val nombre: String,
    val matricula: String,
    val carrera: String,
    val especialidad: String,
    val semestre: String,
    val creditosAcumulados: String
)

fun parsearPerfilJson(jsonStr: String): PerfilAcademico? {
    if (jsonStr.isBlank() || !jsonStr.contains("{")) return null

    val nombre = "\"(?i)nombre\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(jsonStr)?.groupValues?.get(1) ?: "Desconocido"
    val matricula = "\"(?i)matricula\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(jsonStr)?.groupValues?.get(1) ?: "Sin Matrícula"
    val carrera = "\"(?i)carrera\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(jsonStr)?.groupValues?.get(1) ?: "Desconocida"
    val especialidad = "\"(?i)especialidad\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(jsonStr)?.groupValues?.get(1) ?: "N/A"
    val semestre = "\"(?i)semActual\"\\s*:\\s*(\\d+)".toRegex().find(jsonStr)?.groupValues?.get(1) ?: "0"
    val creditos = "\"(?i)cdtosAcumulados\"\\s*:\\s*(\\d+)".toRegex().find(jsonStr)?.groupValues?.get(1) ?: "0"

    return PerfilAcademico(nombre, matricula, carrera, especialidad, semestre, creditos)
}