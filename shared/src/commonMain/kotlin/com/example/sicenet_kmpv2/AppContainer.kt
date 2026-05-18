package com.example.sicenet_kmpv2

import androidx.room3.RoomDatabase
import com.example.sicenet_kmpv2.network.sicenetHttpClient
import com.example.sicenet_kmpv2.data.repository.SicenetRepository
import com.example.sicenet_kmpv2.data.local.SicenetDatabase
import com.example.sicenet_kmpv2.domain.SyncManager

object AppContainer {
    lateinit var database: SicenetDatabase
    lateinit var repository: SicenetRepository
    lateinit var syncManager: com.example.sicenet_kmpv2.domain.SyncManager

    fun inicializar(db: RoomDatabase.Builder<SicenetDatabase>, sync: SyncManager) {
        database = db.build()
        repository = SicenetRepository(sicenetHttpClient)
        syncManager = sync
    }
}