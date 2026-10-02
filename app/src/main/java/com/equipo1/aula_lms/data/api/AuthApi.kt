package com.equipo1.aula_lms.data.api

import com.equipo1.aula_lms.data.model.Usuario
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body datos: LoginRequest): LoginResponse
    @POST("logout")
    suspend fun logout(): Response<Unit>
}

@Serializable
data class LoginRequest(
    @SerialName("numero_cuenta") val numeroCuenta: String,
    val nip: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("user") val usuario: Usuario,
)

/**
 * Cuerpo de los errores de la API: { "message": "..." }.
 */
@Serializable
data class ErrorResponse(val message: String? = null)
