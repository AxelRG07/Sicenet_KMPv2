package com.example.sicenet_kmpv2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet_kmpv2.AppContainer
import com.example.sicenet_kmpv2.data.local.CacheAcademicoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File

class FetchWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val idDato = inputData.getString("ID_DATO") ?: return@withContext Result.failure()
        val parametroExtra = inputData.getInt("PARAM_EXTRA", 0)
        val repo = AppContainer.repository

        val responseResult = when (idDato) {
            "perfil" -> repo.obtenerPerfil()
            "carga_academica" -> repo.obtenerCargaAcademica()
            "calif_unidades" -> repo.obtenerCalifUnidades()
            "kardex" -> repo.obtenerKardex(parametroExtra)
            "calif_final" -> repo.obtenerCalifFinales(parametroExtra)
            else -> return@withContext Result.failure()
        }

        if (responseResult.isSuccess) {
            val rawXml = responseResult.getOrNull() ?: ""

            val cleanJson = rawXml.substringAfter("Result>").substringBefore("</")

            val tempFile = File(applicationContext.cacheDir, "${idDato}_temp.txt")
            tempFile.writeText(cleanJson)

            val outputData = workDataOf(
                "ID_DATO" to idDato,
                "FILE_PATH" to tempFile.absolutePath
            )
            Result.success(outputData)
        } else {
            Result.retry()
        }
    }
}

class SaveWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val idDato = inputData.getString("ID_DATO") ?: return@withContext Result.failure()
        val filePath = inputData.getString("FILE_PATH") ?: return@withContext Result.failure()

        val file = File(filePath)
        if (!file.exists()) return@withContext Result.failure()

        val xmlData = file.readText()
        val dao = AppContainer.database.sicenetDao()

        val cache = CacheAcademicoEntity(
            idDato = idDato,
            contenidoXml = xmlData,
            timestampActualizacion = Clock.System.now().toEpochMilliseconds()
        )
        dao.guardarCache(cache)
        file.delete()

        Result.success()
    }
}