package com.equipo1.aula_lms.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.equipo1.aula_lms.AulaApp
import com.equipo1.aula_lms.data.AuthRepository
import com.equipo1.aula_lms.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val numeroCuenta: String = "",
    val nip: String = "",
    val errorNumeroCuenta: Boolean = false,
    val errorNip: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    // Se llena al iniciar sesión; la pantalla navega con él
    val usuario: Usuario? = null,
    val sesionCerrada: Boolean = false
)

class LoginViewModel(private val repositorio: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onNumeroCuentaChange(valor: String) {
        _estado.update { it.copy(numeroCuenta = valor, errorNumeroCuenta = false, error = null) }
    }

    fun onNipChange(valor: String) {
        _estado.update { it.copy(nip = valor, errorNip = false, error = null) }
    }

    fun iniciarSesion() {
        val numeroCuenta = _estado.value.numeroCuenta.trim()
        val nip = _estado.value.nip

        val errorNumeroCuenta = !numeroCuenta.matches(Regex("\\d+"))
        val errorNip = nip.isBlank()
        if (errorNumeroCuenta || errorNip) {
            _estado.update { it.copy(errorNumeroCuenta = errorNumeroCuenta, errorNip = errorNip) }
            return
        }

        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repositorio.login(numeroCuenta, nip)
                .onSuccess { usuario -> _estado.update { it.copy(cargando = false, usuario = usuario) } }
                .onFailure { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
        }
    }
    
    companion object {
        val Factory = viewModelFactory {
            initializer { LoginViewModel((this[APPLICATION_KEY] as AulaApp).authRepository) }
        }
    }
}
