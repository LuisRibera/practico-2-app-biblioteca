package com.example.practico_2_app_biblioteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practico_2_app_biblioteca.data.model.GeneroDto
import com.example.practico_2_app_biblioteca.data.model.GeneroRequest
import com.example.practico_2_app_biblioteca.data.repository.GeneroRepository
import com.example.practico_2_app_biblioteca.validation.GeneroValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GeneroListUiState {
    object Loading : GeneroListUiState
    data class Success(val generos: List<GeneroDto>) : GeneroListUiState
    data class Error(val message: String) : GeneroListUiState
}

class GeneroViewModel : ViewModel() {

    private val repository = GeneroRepository()

    private val _listState = MutableStateFlow<GeneroListUiState>(GeneroListUiState.Loading)
    val listState: StateFlow<GeneroListUiState> = _listState.asStateFlow()

    private val _formNombre = MutableStateFlow("")
    val formNombre: StateFlow<String> = _formNombre.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun cargarGeneros() {
        viewModelScope.launch {
            _listState.value = GeneroListUiState.Loading
            repository.getGeneros().fold(
                onSuccess = { generos -> _listState.value = GeneroListUiState.Success(generos) },
                onFailure = { e -> _listState.value = GeneroListUiState.Error(e.message ?: "Error desconocido") }
            )
        }
    }

    fun setNombre(v: String) { _formNombre.value = v }

    fun limpiarFormulario() {
        _formNombre.value = ""
        _isSubmitting.value = false
    }

    fun crearGenero() {
        if (_isSubmitting.value) return
        val nombre = _formNombre.value
        val error = GeneroValidator.validarNombre(nombre)
        if (error != null) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowMessage(error)) }
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repository.crearGenero(GeneroRequest(nombre = nombre)).fold(
                    onSuccess = {
                        _uiEvent.emit(UiEvent.ShowMessage("Género creado exitosamente"))
                        _uiEvent.emit(UiEvent.NavigateBack)
                        limpiarFormulario()
                    },
                    onFailure = { e ->
                        _uiEvent.emit(UiEvent.ShowMessage("Error al crear el género: ${e.message}"))
                    }
                )
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun eliminarGenero(id: Int) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repository.eliminarGenero(id).fold(
                    onSuccess = {
                        _uiEvent.emit(UiEvent.ShowMessage("Género eliminado exitosamente"))
                        cargarGeneros()
                    },
                    onFailure = { e ->
                        _uiEvent.emit(UiEvent.ShowMessage("Error al eliminar el género: ${e.message}"))
                    }
                )
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
