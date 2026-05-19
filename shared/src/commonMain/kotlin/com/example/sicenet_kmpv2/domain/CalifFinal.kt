package com.example.sicenet_kmpv2.domain

data class MateriaFinal(
    val materia: String,
    val calificacionFinal: String
)

fun parsearCalifFinalJson(jsonStr: String): List<MateriaFinal> {
    if (jsonStr.isBlank() || !jsonStr.contains("{")) return emptyList()

    val materias = mutableListOf<MateriaFinal>()
    val bloques = "\\{([^}]+)\\}".toRegex().findAll(jsonStr)

    for (bloque in bloques) {
        val content = bloque.groupValues[1]

        val materia = "\"(?i)(?:materia|nombreMateria|nombre)\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(content)?.groupValues?.get(1) ?: "Desconocida"
        val califFinal = "\"(?i)(?:califFinal|calificacionFinal|calif|promedio)\"\\s*:\\s*\"?(\\d+|[A-Z]+)\"?".toRegex().find(content)?.groupValues?.get(1) ?: "N/A"

        materias.add(MateriaFinal(materia, califFinal))
    }
    return materias
}