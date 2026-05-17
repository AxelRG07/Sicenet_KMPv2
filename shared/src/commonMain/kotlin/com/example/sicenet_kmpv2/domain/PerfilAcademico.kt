package com.example.sicenet_kmpv2.domain

data class PerfilAcademico(
    val nombre: String,
    val matricula: String,
    val carrera: String,
    val especialidad: String,
    val semestre: String,
    val creditosAcumulados: String
)

fun parsearPerfilXml(xml: String): PerfilAcademico {
    val jsonCrudo = xml.substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
        .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")

    val nombre = "\"nombre\":\"(.*?)\"".toRegex().find(jsonCrudo)?.groupValues?.get(1) ?: "Desconocido"
    val matricula = "\"matricula\":\"(.*?)\"".toRegex().find(jsonCrudo)?.groupValues?.get(1) ?: "Sin Matrícula"
    val carrera = "\"carrera\":\"(.*?)\"".toRegex().find(jsonCrudo)?.groupValues?.get(1) ?: "Desconocida"
    val especialidad = "\"especialidad\":\"(.*?)\"".toRegex().find(jsonCrudo)?.groupValues?.get(1) ?: "N/A"
    val semestre = "\"semActual\":(\\d+)".toRegex().find(jsonCrudo)?.groupValues?.get(1) ?: "0"
    val creditos = "\"cdtosAcumulados\":(\\d+)".toRegex().find(jsonCrudo)?.groupValues?.get(1) ?: "0"

    return PerfilAcademico(
        nombre = nombre,
        matricula = matricula,
        carrera = carrera,
        especialidad = especialidad,
        semestre = semestre,
        creditosAcumulados = creditos
    )
}
