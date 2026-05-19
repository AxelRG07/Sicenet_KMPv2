package com.example.sicenet_kmpv2.domain

import java.util.prefs.Preferences

class DesktopSessionManager : SessionManager {
    private val prefs = Preferences.userNodeForPackage(DesktopSessionManager::class.java)

    override fun guardarSesion(matricula: String, contrasenia: String) {
        prefs.put("matricula", matricula)
        prefs.put("contrasenia", contrasenia)
        prefs.putBoolean("is_logged_in", true)
    }

    override fun obtenerMatricula(): String? = prefs.get("matricula", null)
    override fun obtenerContrasenia(): String? = prefs.get("contrasenia", null)
    override fun esSesionActiva(): Boolean = prefs.getBoolean("is_logged_in", false)

    override fun cerrarSesion() {
        prefs.clear()
    }
}