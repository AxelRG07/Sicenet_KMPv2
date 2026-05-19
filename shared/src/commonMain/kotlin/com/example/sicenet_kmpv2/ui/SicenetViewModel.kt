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

    private val _califUnidades = MutableStateFlow<CacheAcademicoEntity?>(null)
    val califUnidades: StateFlow<CacheAcademicoEntity?> = _califUnidades.asStateFlow()

    private val _califFinal = MutableStateFlow<CacheAcademicoEntity?>(null)
    val califFinal: StateFlow<CacheAcademicoEntity?> = _califFinal.asStateFlow()

    init {
        viewModelScope.launch {
            dao.observarCache("carga_academica").collect { _cargaAcademica.value = it }
        }
        viewModelScope.launch {
            dao.observarCache("kardex").collect { _kardex.value = it }
        }
        viewModelScope.launch {
            dao.observarCache("calif_unidades").collect { _califUnidades.value = it }
        }
        viewModelScope.launch {
            dao.observarCache("calif_final").collect { _califFinal.value = it }
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

    fun solicitarCalifUnidades() {
        syncManager.sincronizarDato("calif_unidades")
    }

    fun solicitarCalifFinal(modEducativo: Int) = syncManager.sincronizarDato("calif_final", modEducativo)

}