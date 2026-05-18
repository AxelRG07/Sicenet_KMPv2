package com.example.sicenet_kmpv2.domain

data class MateriaKardex(
    val materia: String,
    val calificacion: String,
    val semestre: String
)

fun parsearKardexJson(jsonStr: String): List<MateriaKardex> {
    if (jsonStr.isBlank() || !jsonStr.contains("{")) return emptyList()

    val materias = mutableListOf<MateriaKardex>()
    val bloques = "\\{([^}]+)\\}".toRegex().findAll(jsonStr)

    for (bloque in bloques) {
        val content = bloque.groupValues[1]

        val materia = "\"(?i)(?:materia|nombreMateria|nombre)\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(content)?.groupValues?.get(1) ?: "Desconocida"
        val calif = "\"(?i)(?:calificacion|calif)\"\\s*:\\s*\"?(\\d+|[A-Z]+)\"?".toRegex().find(content)?.groupValues?.get(1) ?: "N/A"
        val semestre = "\"(?i)(?:semestre|sem)\"\\s*:\\s*\"?(\\d+)\"?".toRegex().find(content)?.groupValues?.get(1) ?: "-"

        materias.add(MateriaKardex(materia, calif, semestre))
    }
    return materias
}