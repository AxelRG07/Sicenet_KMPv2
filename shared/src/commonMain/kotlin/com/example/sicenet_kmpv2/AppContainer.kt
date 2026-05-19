package com.example.sicenet_kmpv2

import androidx.room3.RoomDatabase
import com.example.sicenet_kmpv2.data.repository.SicenetRepository
import com.example.sicenet_kmpv2.data.local.SicenetDatabase
import com.example.sicenet_kmpv2.domain.SessionManager
import com.example.sicenet_kmpv2.domain.SyncManager
import com.example.sicenet_kmpv2.network.crearClienteHttp

object AppContainer {
    lateinit var database: SicenetDatabase
    lateinit var repository: SicenetRepository
    lateinit var syncManager: SyncManager
    lateinit var sessionManager: SessionManager

    fun inicializar(db: RoomDatabase.Builder<SicenetDatabase>, sync: SyncManager, session: SessionManager) {
        database = db.build()
        repository = SicenetRepository(crearClienteHttp())
        syncManager = sync
        sessionManager = session
    }

    fun limpiarRed() {
        repository = SicenetRepository(crearClienteHttp())
    }
}