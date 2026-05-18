package com.example.sicenet_kmpv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet_kmpv2.AppContainer
import com.example.sicenet_kmpv2.data.repository.SicenetRepository
import com.example.sicenet_kmpv2.domain.PerfilAcademico
import com.example.sicenet_kmpv2.ui.SicenetViewModel
import kotlinx.coroutines.launch

@Composable
fun SicenetApp(repository: SicenetRepository) {
    var isAuthenticated by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        LoginScreen(repository) {
            isAuthenticated = true
        }
    } else {
        MainDashboard()
    }
}

@Composable
fun MainDashboard(viewModel: SicenetViewModel = SicenetViewModel()) {
    var pantallaActual by remember { mutableStateOf("perfil") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = pantallaActual == "perfil",
                    onClick = { pantallaActual = "perfil" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Carga") },
                    label = { Text("Carga") },
                    selected = pantallaActual == "carga",
                    onClick = { pantallaActual = "carga" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Kardex") },
                    label = { Text("Kardex") },
                    selected = pantallaActual == "kardex",
                    onClick = { pantallaActual = "kardex" }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (pantallaActual) {
                "perfil" -> ProfileScreen(AppContainer.repository)
                "carga" -> CargaAcademicaScreen(viewModel)
                "kardex" -> KardexScreen(viewModel)
            }
        }
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

                    Text("Carrera", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
                    Text(perfil!!.carrera, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Especialidad", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
                    Text(perfil!!.especialidad, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, ) {
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