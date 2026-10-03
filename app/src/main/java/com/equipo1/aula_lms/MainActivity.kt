package com.equipo1.aula_lms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.equipo1.aula_lms.data.api.RedLocal
import com.equipo1.aula_lms.ui.navegacion.AppNavHost
import com.equipo1.aula_lms.ui.theme.AulalmsTheme

class MainActivity : ComponentActivity() {

    private val pedirPermisoRedLocal =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (RedLocal.necesitaPermiso(this)) {
            pedirPermisoRedLocal.launch(RedLocal.PERMISO)
        }

        val app = application as AulaApp
        setContent {
            AulalmsTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    AppNavHost(
                        authRepository = app.authRepository,
                        sesionExpirada = app.api.sesionExpirada,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
