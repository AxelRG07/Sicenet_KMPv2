package com.example.sicenet_kmpv2.domain

interface SyncManager {
    fun sincronizarDato(idDato: String, parametroExtra: Int? = null)
}