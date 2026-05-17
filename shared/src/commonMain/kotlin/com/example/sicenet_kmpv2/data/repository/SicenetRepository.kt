package com.example.sicenet_kmpv2.data.repository

import com.example.sicenet_kmpv2.domain.PerfilAcademico
import com.example.sicenet_kmpv2.domain.parsearPerfilXml
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class SicenetRepository(private val client: HttpClient) {

    private val baseUrl = "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx"

    private var sessionCookie: String? = null

    suspend fun login(matricula: String, contrasenia: String): Result<String> {
        val requestBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <accesoLogin xmlns="http://tempuri.org/">
                  <strMatricula><![CDATA[$matricula]]></strMatricula>
                  <strContrasenia><![CDATA[$contrasenia]]></strContrasenia>
                  <tipoUsuario>ALUMNO</tipoUsuario>
                </accesoLogin>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.parse("text/xml; charset=utf-8"))
                header("SOAPAction", "\"http://tempuri.org/accesoLogin\"")
                setBody(requestBody)
            }

            val setCookieHeaders = response.headers.getAll("Set-Cookie")
            if (!setCookieHeaders.isNullOrEmpty()) {
                val allCookies = setCookieHeaders.mapNotNull { it.split(";").firstOrNull() }
                sessionCookie = allCookies.joinToString("; ")
                println("Cookies combinadas capturadas: $sessionCookie")
            } else {
                println("Advertencia: No se recibieron cookies.")
            }

            val responseText = response.bodyAsText()
            println("Respuesta Login Cruda: $responseText")

            if (responseText.contains("soap:Fault")) {
                Result.failure(Exception("Error de servidor SOAP"))
            } else if (responseText.contains("\"acceso\":false")) {
                Result.failure(Exception("Usuario o contraseña incorrectos"))
            } else if (responseText.contains("Object moved") && sessionCookie?.contains("ASP.NET_SessionId") == true) {
                Result.success("Login exitoso (Redirección ASP.NET)")
            } else if (responseText.contains("accesoLoginResult")) {
                Result.success(responseText)
            } else {
                Result.failure(Exception("Respuesta no reconocida del servidor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPerfil(): Result<PerfilAcademico> {
        val requestBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.parse("text/xml; charset=utf-8"))
                header("SOAPAction", "\"http://tempuri.org/getAlumnoAcademicoWithLineamiento\"")

                if (sessionCookie != null) {
                    header("Cookie", sessionCookie)
                }

                setBody(requestBody)
            }

            val responseText = response.bodyAsText()

            if (responseText.contains("soap:Fault")) {
                Result.failure(Exception("Error de SOAP en Perfil"))
            } else {
                val perfil = parsearPerfilXml(responseText)
                Result.success(perfil)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 1. CARGA ACADÉMICA
    suspend fun obtenerCargaAcademica(): Result<String> {
        val requestBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return ejecutarPeticionSoap("getCargaAcademicaByAlumno", requestBody)
    }

    // 2. CALIFICACIONES POR UNIDAD (Parciales)
    suspend fun obtenerCalifUnidades(): Result<String> {
        val requestBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return ejecutarPeticionSoap("getCalifUnidadesByAlumno", requestBody)
    }

    // 3. KARDEX
    suspend fun obtenerKardex(lineamiento: Int): Result<String> {
        val requestBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
                  <aluLineamiento>$lineamiento</aluLineamiento>
                </getAllKardexConPromedioByAlumno>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return ejecutarPeticionSoap("getAllKardexConPromedioByAlumno", requestBody)
    }

    // 4. CALIFICACIÓN FINAL (Requiere el modelo educativo)
    suspend fun obtenerCalifFinales(modEducativo: Int): Result<String> {
        val requestBody = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
                  <bytModEducativo>$modEducativo</bytModEducativo>
                </getAllCalifFinalByAlumnos>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        return ejecutarPeticionSoap("getAllCalifFinalByAlumnos", requestBody)
    }

    // FUNCIÓN AUXILIAR PARA NO REPETIR CÓDIGO
    private suspend fun ejecutarPeticionSoap(soapAction: String, requestBody: String): Result<String> {
        return try {
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.parse("text/xml; charset=utf-8"))
                header("SOAPAction", "\"http://tempuri.org/$soapAction\"")
                if (sessionCookie != null) header("Cookie", sessionCookie)
                setBody(requestBody)
            }
            val responseText = response.bodyAsText()
            if (responseText.contains("soap:Fault")) {
                Result.failure(Exception("Error de SOAP en $soapAction"))
            } else {
                Result.success(responseText)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

