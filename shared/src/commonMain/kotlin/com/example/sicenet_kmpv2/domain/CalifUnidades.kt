package com.example.sicenet_kmpv2.domain

data class MateriaParciales(
    val materia: String,
    val calificaciones: Map<String, String>
)

fun parsearCalifUnidadesJson(jsonStr: String): List<MateriaParciales> {
    if (jsonStr.isBlank() || !jsonStr.contains("{")) return emptyList()

    val materias = mutableListOf<MateriaParciales>()
    val bloques = "\\{([^}]+)\\}".toRegex().findAll(jsonStr)

    for (bloque in bloques) {
        val content = bloque.groupValues[1]

        val materia = "\"(?i)(?:materia|nombreMateria|nombre)\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(content)?.groupValues?.get(1) ?: "Desconocida"

        val calificaciones = mutableMapOf<String, String>()

        for (i in 1..13) {
            val calif = "\"(?i)c$i\"\\s*:\\s*\"?([A-Z0-9]+)\"?".toRegex().find(content)?.groupValues?.get(1)

            if (calif != null && calif.isNotBlank() && calif != "null") {
                calificaciones["U$i"] = calif
            }
        }

        materias.add(MateriaParciales(materia, calificaciones))
    }
    return materias
}