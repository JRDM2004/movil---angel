package com.equipo1.aula_lms.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.equipo1.aula_lms.data.AuthRepository
import com.equipo1.aula_lms.data.model.Rol
import com.equipo1.aula_lms.data.model.Usuario
import com.equipo1.aula_lms.ui.inicio.AvisoAdminScreen
import com.equipo1.aula_lms.ui.inicio.InicioScreen
import com.equipo1.aula_lms.ui.login.LoginScreen

private object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AVISO_ADMIN = "aviso-admin"
}

private fun rutaInicio(usuario: Usuario) =
    if (usuario.rol == Rol.ADMINISTRADOR) Rutas.AVISO_ADMIN else Rutas.INICIO

@Composable
fun AppNavHost(authRepository: AuthRepository, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val usuarioGuardado = authRepository.usuarioActual()

    NavHost(
        navController = navController,
        startDestination = usuarioGuardado?.let(::rutaInicio) ?: Rutas.LOGIN,
        modifier = modifier,
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    // Se quita el login de la pila para que "atrás" no regrese a él
                    navController.navigate(rutaInicio(usuario)) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(Rutas.INICIO) {
            authRepository.usuarioActual()?.let { usuario ->
                InicioScreen(
                    usuario = usuario,
                    onSesionCerrada = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
        }
        composable(Rutas.AVISO_ADMIN) {
            authRepository.usuarioActual()?.let { AvisoAdminScreen(it) }
        }
    }
}
