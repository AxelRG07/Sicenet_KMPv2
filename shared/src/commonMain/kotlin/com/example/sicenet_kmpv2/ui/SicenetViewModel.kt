package com.example.sicenet_kmpv2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet_kmpv2.AppContainer
import com.example.sicenet_kmpv2.data.local.CacheAcademicoEntity
import com.example.sicenet_kmpv2.utils.formatearFechaNativa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SicenetViewModel : ViewModel() {
    private val dao = AppContainer.database.sicenetDao()
    private val syncManager = AppContainer.syncManager

    private val _cargaAcademica = MutableStateFlow<CacheAcademicoEntity?>(null)
    val cargaAcademica: StateFlow<CacheAcademicoEntity?> = _cargaAcademica.asStateFlow()

    private val _kardex = MutableStateFlow<CacheAcademicoEntity?>(null)
    val kardex: StateFlow<CacheAcademicoEntity?> = _kardex.asStateFlow()

    init {
        viewModelScope.launch {
            dao.observarCache("carga_academica").collect { _cargaAcademica.value = it }
        }
        viewModelScope.launch {
            dao.observarCache("kardex").collect { _kardex.value = it }
        }
    }

    fun solicitarCargaAcademica() {
        syncManager.sincronizarDato("carga_academica")
    }

    fun solicitarKardex(lineamiento: Int) {
        syncManager.sincronizarDato("kardex", lineamiento)
    }

    fun formatearFecha(timestamp: Long): String {
        return formatearFechaNativa(timestamp)
    }
}