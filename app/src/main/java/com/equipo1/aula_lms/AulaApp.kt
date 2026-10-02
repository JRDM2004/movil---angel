package com.equipo1.aula_lms

import android.app.Application
import com.equipo1.aula_lms.data.AuthRepository
import com.equipo1.aula_lms.data.SessionStorage
import com.equipo1.aula_lms.data.api.ApiClient

/**
 * Crea una sola vez las dependencias que comparten las pantallas.
 */
class AulaApp : Application() {
    val sesion by lazy { SessionStorage(this) }
    val api by lazy { ApiClient(sesion) }
    val authRepository by lazy { AuthRepository(api.auth, sesion) }
}
