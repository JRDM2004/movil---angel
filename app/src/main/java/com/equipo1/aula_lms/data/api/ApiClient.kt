package com.equipo1.aula_lms.data.api

import com.equipo1.aula_lms.BuildConfig
import com.equipo1.aula_lms.data.SessionStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

val json = Json { ignoreUnknownKeys = true }

class ApiClient(sesion: SessionStorage) {

    private val _sesionExpirada = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Emite cuando la API rechaza el token (401): la sesión ya se borró y
     * la interfaz debe regresar al login.
     */
    val sesionExpirada: SharedFlow<Unit> = _sesionExpirada.asSharedFlow()

    // Agrega el token a cada petición si hay sesión iniciada
    private val http = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = sesion.token()
            val peticion = chain.request().newBuilder()
                .header("Accept", "application/json")
            token?.let { peticion.header("Authorization", "Bearer $it") }

            val respuesta = chain.proceed(peticion.build())

            // Un 401 sin token (NIP incorrecto en el login) lo maneja quien hizo la petición, y
            // el logout ya borra la sesión y navega por su cuenta. Se compara con el token actual
            // para no borrar una sesión nueva por una respuesta vieja.
            val esLogout = chain.request().url.encodedPath.endsWith("/logout")
            if (respuesta.code == 401 && token != null && !esLogout && token == sesion.token()) {
                sesion.borrar()
                _sesionExpirada.tryEmit(Unit)
            }
            respuesta
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .client(http)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val auth: AuthApi = retrofit.create()
}
