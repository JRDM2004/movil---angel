package com.equipo1.aula_lms.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.equipo1.aula_lms.data.api.json
import com.equipo1.aula_lms.data.model.Usuario
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Guarda la sesión del usuario. El token se cifra con una llave del Android
 * Keystore, que no sale del dispositivo; los datos del usuario no son
 * sensibles y se guardan tal cual.
 */
class SessionStorage(context: Context) {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardar(token: String, usuario: Usuario) {
        prefs.edit {
            putString(TOKEN, cifrar(token))
            putString(USUARIO, json.encodeToString(Usuario.serializer(), usuario))
        }
    }

    fun token(): String? =
        prefs.getString(TOKEN, null)?.let { runCatching { descifrar(it) }.getOrNull() }

    /**
     * Devuelve null si no hay sesión o si lo guardado ya no es válido.
     */
    fun usuario(): Usuario? {
        if (token() == null) return null

        val usuario = prefs.getString(USUARIO, null)?.let {
            runCatching { json.decodeFromString(Usuario.serializer(), it) }.getOrNull()
        }
        if (usuario == null) borrar()
        return usuario
    }

    fun borrar() {
        prefs.edit { clear() }
    }

    private fun llave(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(ALIAS, null) as? SecretKey)?.let { return it }

        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generador.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return generador.generateKey()
    }

    // Se guarda el IV (12 bytes) seguido del texto cifrado
    private fun cifrar(texto: String): String {
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.ENCRYPT_MODE, llave())
        return Base64.encodeToString(cipher.iv + cipher.doFinal(texto.toByteArray()), Base64.NO_WRAP)
    }

    private fun descifrar(guardado: String): String {
        val bytes = Base64.decode(guardado, Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.DECRYPT_MODE, llave(), GCMParameterSpec(128, bytes, 0, 12))
        return String(cipher.doFinal(bytes, 12, bytes.size - 12))
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val ALIAS = "aula_uas_token"
        const val TRANSFORMACION = "AES/GCM/NoPadding"
        const val TOKEN = "token"
        const val USUARIO = "usuario"
    }
}
