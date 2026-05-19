package com.example.sicenet_kmpv2.domain

import android.content.Context

class AndroidSessionManager(context: Context) : SessionManager {
    private val prefs = context.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)

    override fun guardarSesion(matricula: String, contrasenia: String) {
        prefs.edit()
            .putString("matricula", matricula)
            .putString("contrasenia", contrasenia)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    override fun obtenerMatricula(): String? = prefs.getString("matricula", null)
    override fun obtenerContrasenia(): String? = prefs.getString("contrasenia", null)
    override fun esSesionActiva(): Boolean = prefs.getBoolean("is_logged_in", false)

    override fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}