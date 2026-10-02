package com.equipo1.aula_lms.data.api

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.equipo1.aula_lms.BuildConfig

/**
 * Desde Android 17, una app necesita el permiso ACCESS_LOCAL_NETWORK para
 * conectarse a direcciones de la red local (10.x, 172.16-31.x, 192.168.x),
 * como la API corriendo en la PC de desarrollo. Sin él, la conexión se
 * descarta sin aviso y termina en timeout.
 */
object RedLocal {
    const val PERMISO = "android.permission.ACCESS_LOCAL_NETWORK"

    fun necesitaPermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT >= 37 &&
            esDireccionLocal(Uri.parse(BuildConfig.API_URL).host) &&
            context.checkSelfPermission(PERMISO) != PackageManager.PERMISSION_GRANTED

    fun esDireccionLocal(host: String?): Boolean {
        if (host == null) return false
        if (host.endsWith(".local")) return true

        val partes = host.split(".").mapNotNull { it.toIntOrNull() }
        if (partes.size != 4) return false
        val (a, b) = partes
        return a == 10 || (a == 172 && b in 16..31) || (a == 192 && b == 168)
    }
}
