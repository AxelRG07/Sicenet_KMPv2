package com.example.sicenet_kmpv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet_kmpv2.data.repository.SicenetRepository
import com.example.sicenet_kmpv2.domain.PerfilAcademico
import kotlinx.coroutines.launch

@Composable
fun SicenetApp(repository: SicenetRepository) {
    var isAuthenticated by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        LoginScreen(repository) {
            isAuthenticated = true
        }
    } else {
        ProfileScreen(repository)
    }
}

@Composable
fun LoginScreen(repository: SicenetRepository, onLoginSuccess: () -> Unit) {
    var matricula by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Acceso a SICENET", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            label = { Text("Matrícula") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                coroutineScope.launch {
                    val result = repository.login(matricula, password)
                    isLoading = false
                    if (result.isSuccess) {
                        val xml = result.getOrNull()
                        println("XML recibido: $xml")
                        onLoginSuccess()
                    } else {
                        val errorReal = result.exceptionOrNull()?.message ?: "Error desconocido"
                        errorMessage = "Error: $errorReal"
                        println("Ktor Error: ${result.exceptionOrNull()}")
                    }
                }
            },
            enabled = !isLoading && matricula.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Iniciar Sesión")
        }
    }
}

@Composable
fun ProfileScreen(repository: SicenetRepository) {
    var perfil by remember { mutableStateOf<PerfilAcademico?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = repository.obtenerPerfil()
            if (result.isSuccess) {
                perfil = result.getOrNull()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil SICENET", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else if (perfil == null) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Extrayendo datos académicos...")
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                            Text(perfil!!.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Matrícula: ${perfil!!.matricula}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    Text("Carrera", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(perfil!!.carrera, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Especialidad", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(perfil!!.especialidad, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Semestre", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(perfil!!.semestre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Créditos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(perfil!!.creditosAcumulados, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }