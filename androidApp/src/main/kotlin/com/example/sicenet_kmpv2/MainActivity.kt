package com.example.sicenet_kmpv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.sicenet_kmpv2.data.local.SicenetDatabase
import com.example.sicenet_kmpv2.ui.screens.SicenetApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContext = applicationContext
        val dbFile = appContext.getDatabasePath("sicenet.db")
        val db = Room.databaseBuilder<SicenetDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        ).setDriver(BundledSQLiteDriver())

        val syncManager = AndroidSyncManager(appContext)

        AppContainer.inicializar(db, syncManager)

        setContent {
            SicenetApp(repository = AppContainer.repository)
        }
    }
}