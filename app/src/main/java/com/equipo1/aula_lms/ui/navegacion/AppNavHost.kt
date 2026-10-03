package com.equipo1.aula_lms.ui.navegacion

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.Flow

private object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AVISO_ADMIN = "aviso-admin"
}

private fun rutaInicio(usuario: Usuario) =
    if (usuario.rol == Rol.ADMINISTRADOR) Rutas.AVISO_ADMIN else Rutas.INICIO

@Composable
fun AppNavHost(
    authRepository: AuthRepository,
    sesionExpirada: Flow<Unit>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    // Se calcula una sola vez: si la sesión se borra después, la navegación la manejan
    // el logout y el efecto de abajo
    val rutaInicial = remember { authRepository.usuarioActual()?.let(::rutaInicio) ?: Rutas.LOGIN }

    // Manejo global del 401: la sesión ya se borró en ApiClient, aquí solo se regresa al login
    LaunchedEffect(sesionExpirada) {
        sesionExpirada.collect {
            if (navController.currentDestination?.route == Rutas.LOGIN) return@collect

            navController.navigate(Rutas.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
            snackbarHostState.showSnackbar("Tu sesión expiró. Vuelve a iniciar sesión.")
        }
    }

    NavHost(
        navController = navController,
        startDestination = rutaInicial,
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
