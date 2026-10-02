package com.equipo1.aula_lms.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = UasAzulClaro,
    secondary = UasOroClaro,
    tertiary = UasOroClaro,
)

private val LightColorScheme = lightColorScheme(
    primary = UasAzul,
    secondary = UasOro,
    tertiary = UasMostaza,
)

// Sin colores dinámicos de Android 12+ para conservar los colores de la UAS
@Composable
fun AulalmsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
