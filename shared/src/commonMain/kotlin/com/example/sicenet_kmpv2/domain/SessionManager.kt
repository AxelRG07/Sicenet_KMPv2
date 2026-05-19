package com.example.sicenet_kmpv2.domain

interface SessionManager {
    fun guardarSesion(matricula: String, contrasenia: String)
    fun obtenerMatricula(): String?
    fun obtenerContrasenia(): String?
    fun esSesionActiva(): Boolean
    fun cerrarSesion()
}