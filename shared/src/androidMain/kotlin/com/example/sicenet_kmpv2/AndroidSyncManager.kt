package com.example.sicenet_kmpv2

import android.content.Context
import androidx.work.*
import com.example.sicenet_kmpv2.domain.SyncManager
import com.example.sicenet_kmpv2.workers.FetchWorker
import com.example.sicenet_kmpv2.workers.SaveWorker

class AndroidSyncManager(private val context: Context) : SyncManager {

    override fun sincronizarDato(idDato: String, parametroExtra: Int?) {
        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val fetchInput = Data.Builder()
            .putString("ID_DATO", idDato)
            .apply { parametroExtra?.let { putInt("PARAM_EXTRA", it) } }
            .build()

        val fetchWork = OneTimeWorkRequestBuilder<FetchWorker>()
            .setConstraints(constraints)
            .setInputData(fetchInput)
            .build()

        val saveWork = OneTimeWorkRequestBuilder<SaveWorker>()
            .build()

        workManager.beginUniqueWork(
            "SYNC_$idDato",
            ExistingWorkPolicy.REPLACE,
            fetchWork
        ).then(saveWork).enqueue()
    }
}