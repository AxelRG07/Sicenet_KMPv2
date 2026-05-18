package com.example.sicenet_kmpv2.domain

data class MateriaCarga(
    val materia: String,
    val grupo: String,
    val creditos: String
)

fun parsearCargaJson(jsonStr: String): List<MateriaCarga> {
    if (jsonStr.isBlank() || !jsonStr.contains("{")) return emptyList()

    val materias = mutableListOf<MateriaCarga>()
    val bloques = "\\{([^}]+)\\}".toRegex().findAll(jsonStr)

    for (bloque in bloques) {
        val content = bloque.groupValues[1]

        val nombre = "\"(?i)materia\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(content)?.groupValues?.get(1) ?: "Desconocida"
        val grupo = "\"(?i)grupo\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(content)?.groupValues?.get(1) ?: "-"

        val creditos = "\"(?i)creditos\"\\s*:\\s*\"?(\\d+)\"?".toRegex().find(content)?.groupValues?.get(1) ?: "0"

        materias.add(MateriaCarga(nombre, grupo, creditos))
    }
    return materias
}