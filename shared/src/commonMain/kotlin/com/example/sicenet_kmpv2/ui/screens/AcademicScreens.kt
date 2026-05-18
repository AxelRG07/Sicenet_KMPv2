package com.example.sicenet_kmpv2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet_kmpv2.domain.parsearCargaJson
import com.example.sicenet_kmpv2.domain.parsearKardexJson
import com.example.sicenet_kmpv2.ui.SicenetViewModel

@Composable
fun CargaAcademicaScreen(viewModel: SicenetViewModel) {
    val cargaCache by viewModel.cargaAcademica.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.solicitarCargaAcademica()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Carga Académica", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (cargaCache != null) {
            Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                Text(
                    "Última actualización: ${viewModel.formatearFecha(cargaCache!!.timestampActualizacion)}",
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val listaMaterias = parsearCargaJson(cargaCache!!.contenidoXml)

            if (listaMaterias.isEmpty()) {
                Text("No se encontraron materias en tu carga actual.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaMaterias) { materia ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(materia.materia, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Grupo: ${materia.grupo} | Créditos: ${materia.creditos}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Sincronizando...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun KardexScreen(viewModel: SicenetViewModel) {
    val kardexCache by viewModel.kardex.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.solicitarKardex(3)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial Kardex", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (kardexCache != null) {
            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                Text(
                    "Última actualización: ${viewModel.formatearFecha(kardexCache!!.timestampActualizacion)}",
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val listaKardex = parsearKardexJson(kardexCache!!.contenidoXml)

            if (listaKardex.isEmpty()) {
                Text("No se encontraron materias en tu Kardex histórico.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaKardex) { materia ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(materia.materia, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Semestre: ${materia.semestre}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = materia.calificacion,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((materia.calificacion.toIntOrNull() ?: 0) >= 70)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Sincronizando...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}