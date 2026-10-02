package com.equipo1.aula_lms.data

import android.util.Log
import com.equipo1.aula_lms.data.api.AuthApi
import com.equipo1.aula_lms.data.api.ErrorResponse
import com.equipo1.aula_lms.data.api.LoginRequest
import com.equipo1.aula_lms.data.api.json
import com.equipo1.aula_lms.data.model.Usuario
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthApi,
    private val sesion: SessionStorage,
) {
    fun usuarioActual(): Usuario? = sesion.usuario()

    suspend fun login(numeroCuenta: String, nip: String): Result<Usuario> =
        try {
            val respuesta = api.login(LoginRequest(numeroCuenta, nip))
            sesion.guardar(respuesta.token, respuesta.usuario)
            Result.success(respuesta.usuario)
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDeError(e)))
        } catch (e: IOException) {
            Log.w(TAG, "Error de red al iniciar sesión", e)
            Result.failure(Exception("No se pudo conectar con el servidor. Intenta de nuevo más tarde."))
        }

    suspend fun logout(): Result<Unit> {
        val resultado = runCatching { api.logout() }
        sesion.borrar() //
        return resultado.map { }
    }
    // Muestra el mensaje que manda la API (credenciales incorrectas, cuenta desactivada, etc.)
    private fun mensajeDeError(e: HttpException): String {
        val cuerpo = e.response()?.errorBody()?.string()
        val mensaje = cuerpo?.let {
            runCatching { json.decodeFromString(ErrorResponse.serializer(), it).message }.getOrNull()
        }
        return mensaje ?: "Error al iniciar sesión (HTTP ${e.code()})."
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
