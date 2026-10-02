package com.equipo1.aula_lms.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.equipo1.aula_lms.AulaApp
import com.equipo1.aula_lms.data.AuthRepository
import com.equipo1.aula_lms.ui.login.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SesionViewModel(private val repositorio: AuthRepository) : ViewModel() {
    private val _sesionCerrada = MutableStateFlow(false)
    private val _estado = MutableStateFlow(LoginUiState())
    val sesionCerrada: StateFlow<Boolean> = _sesionCerrada.asStateFlow()

    fun cerrarSesion() {
        viewModelScope.launch {
            repositorio.logout()
            _sesionCerrada.value = true
        }
    }

    fun onSesionCerradaConsumida() {
        _estado.update { it.copy(sesionCerrada = false) }
    }
    companion object {
        val Factory = viewModelFactory {
            initializer { SesionViewModel((this[APPLICATION_KEY] as AulaApp).authRepository) }
        }
    }
}