package com.equipo1.aula_lms.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.aula_lms.data.model.Usuario

/**
 * Pantalla provisional de inicio de docentes y alumnos (HU-01).
 *
 */
@Composable
fun InicioScreen(usuario: Usuario, onSesionCerrada: () -> Unit, modifier: Modifier = Modifier, viewModel: SesionViewModel = viewModel(factory = SesionViewModel.Factory) ) {

    val sesionCerrada by viewModel.sesionCerrada.collectAsState()

    LaunchedEffect(sesionCerrada) {
        if (sesionCerrada) onSesionCerrada()
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        Text("Mis cursos", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${usuario.nombre} · ${usuario.numeroCuenta} · ${usuario.rol.name.lowercase()}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = viewModel::cerrarSesion) {
            Text("Cerrar sesion")
        }
    }

}

/**
 * El panel del administrador solo existe en la web.
 */
@Composable
fun AvisoAdminScreen(usuario: Usuario, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Hola, ${usuario.nombre}", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "El panel de administración está disponible solo en la versión web.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
