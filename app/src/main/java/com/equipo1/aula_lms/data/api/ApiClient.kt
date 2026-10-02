package com.equipo1.aula_lms.data.api

import com.equipo1.aula_lms.BuildConfig
import com.equipo1.aula_lms.data.SessionStorage
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

val json = Json { ignoreUnknownKeys = true }

class ApiClient(sesion: SessionStorage) {

    // Agrega el token a cada petición si hay sesión iniciada
    private val http = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val peticion = chain.request().newBuilder()
                .header("Accept", "application/json")
            sesion.token()?.let { peticion.header("Authorization", "Bearer $it") }
            chain.proceed(peticion.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .client(http)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val auth: AuthApi = retrofit.create()
}
