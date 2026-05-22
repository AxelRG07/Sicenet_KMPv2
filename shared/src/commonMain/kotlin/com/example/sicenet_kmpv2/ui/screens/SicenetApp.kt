package com.example.sicenet_kmpv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet_kmpv2.AppContainer
import com.example.sicenet_kmpv2.domain.parsearPerfilJson
import com.example.sicenet_kmpv2.ui.SicenetViewModel
import kotlinx.coroutines.launch

@Composable
fun SicenetApp() {
    val sessionManager = AppContainer.sessionManager

    var isAuthenticated by remember { mutableStateOf(sessionManager.esSesionActiva()) }

    var isRestoringSession by remember { mutableStateOf(sessionManager.esSesionActiva()) }

    LaunchedEffect(Unit) {
        if (sessionManager.esSesionActiva()) {

            val matricula = sessionManager.obtenerMatricula()?.trim() ?: ""
            val contra = sessionManager.obtenerContrasenia()?.trim() ?: ""

            if (matricula.isEmpty() || contra.isEmpty()) {
                sessionManager.cerrarSesion()
                isAuthenticated = false
                isRestoringSession = false
                return@LaunchedEffect
            }

            var intentos = 0
            var exito = false

            while (intentos < 2 && !exito) {
                val result = AppContainer.repository.login(matricula, contra)
                if (result.isSuccess) {
                    exito = true
                } else {
                    intentos++
                    if (intentos < 2) kotlinx.coroutines.delay(500)
                }
            }

            isRestoringSession = false
        }
    }

    if (isRestoringSession) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Conectando...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else if (!isAuthenticated) {
        LoginScreen() { matriculaInsertada, contraseniaInsertada ->
            sessionManager.guardarSesion(matriculaInsertada, contraseniaInsertada)
            isAuthenticated = true
        }
    } else {
        val viewModel = remember { SicenetViewModel() }
        MainDashboard(viewModel = viewModel) {
            isAuthenticated = false
        }
    }
}

@Composable
fun MainDashboard(viewModel: SicenetViewModel = SicenetViewModel(), onLogoutSuccess: () -> Unit) {
    var pantallaActual by remember { mutableStateOf("perfil") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SICEDroid-KMP", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.cerrarSesion(onLogoutSuccess) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
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

                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Parciales") },
                    label = { Text("Parciales") },
                    selected = pantallaActual == "parciales",
                    onClick = { pantallaActual = "parciales" }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Finales") },
                    label = { Text("Finales") },
                    selected = pantallaActual == "finales",
                    onClick = { pantallaActual = "finales" }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (pantallaActual) {
                "perfil" -> ProfileScreen(viewModel)
                "carga" -> CargaAcademicaScreen(viewModel)
                "kardex" -> KardexScreen(viewModel)
                "parciales" -> CalifUnidadesScreen(viewModel)
                "finales" -> CalifFinalScreen(viewModel)
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (matricula: String, password: String) -> Unit) {
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
                    val result = AppContainer.repository.login(matricula, password)
                    isLoading = false
                    if (result.isSuccess) {
                        val xml = result.getOrNull()
                        println("XML recibido: $xml")
                        onLoginSuccess(matricula.trim(), password.trim())
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
fun ProfileScreen(viewModel: SicenetViewModel) {
    val perfilCache by viewModel.perfil.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.precargarDatos()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (perfilCache != null) {
            Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                Text(
                    "Última actualización: ${viewModel.formatearFecha(perfilCache!!.timestampActualizacion)}",
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val perfil = parsearPerfilJson(perfilCache!!.contenidoXml)

            if (perfil == null) {
                Text("Error al procesar el perfil.", color = MaterialTheme.colorScheme.error)
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
        else {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Sincronizando...")
        }
    }
}