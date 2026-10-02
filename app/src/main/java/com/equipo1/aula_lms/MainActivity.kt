package com.equipo1.aula_lms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        authRepository = app.authRepository,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
