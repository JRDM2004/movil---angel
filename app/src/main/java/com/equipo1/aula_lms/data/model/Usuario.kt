package com.equipo1.aula_lms.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Rol {
    @SerialName("alumno") ALUMNO,
    @SerialName("docente") DOCENTE,
    @SerialName("administrador") ADMINISTRADOR,
}

@Serializable
data class Usuario(
    val id: Long,
    @SerialName("numero_cuenta") val numeroCuenta: String,
    val nombre: String,
    val rol: Rol,
)
