package com.example.sicenet_kmpv2

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.sicenet_kmpv2.data.local.SicenetDatabase
import com.example.sicenet_kmpv2.data.local.CacheAcademicoEntity
import com.example.sicenet_kmpv2.domain.SyncManager
import com.example.sicenet_kmpv2.ui.screens.SicenetApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Clock

class DesktopSyncManager : SyncManager {
    override fun sincronizarDato(idDato: String, parametroExtra: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            val repo = AppContainer.repository
            val dao = AppContainer.database.sicenetDao()

            val response = when(idDato) {
                "carga_academica" -> repo.obtenerCargaAcademica()
                "kardex" -> repo.obtenerKardex(parametroExtra ?: 0)
                "calif_unidades" -> repo.obtenerCalifUnidades()
                "calif_final" -> repo.obtenerCalifFinales(parametroExtra ?: 0)
                else -> null
            }

            response?.getOrNull()?.let { xml ->

                val cleanJson = if (idDato != "perfil") {
                    xml.substringAfter("Result>").substringBefore("</")
                } else xml

                dao.guardarCache(
                    CacheAcademicoEntity(
                        idDato = idDato,
                        contenidoXml = cleanJson,
                        timestampActualizacion = Clock.System.now().toEpochMilliseconds()
                    )
                )
            }
        }
    }
}

fun main() = application {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "sicenet.db")
    val db = Room.databaseBuilder<SicenetDatabase>(
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver())

    val syncManager = DesktopSyncManager()

    AppContainer.inicializar(db, syncManager)

    Window(onCloseRequest = ::exitApplication, title = "SICEDroid") {
        SicenetApp(repository = AppContainer.repository)
    }
}