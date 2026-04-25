package com.example.practico_2_app_biblioteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practico_2_app_biblioteca.data.model.LibroDto
import com.example.practico_2_app_biblioteca.data.repository.LibroRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LibroListUiState {
    object Loading : LibroListUiState
    data class Success(val libros: List<LibroDto>) : LibroListUiState
    data class Error(val message: String) : LibroListUiState
}

class LibroViewModel : ViewModel() {

    private val repository = LibroRepository()

    private val _listState = MutableStateFlow<LibroListUiState>(LibroListUiState.Loading)
    val listState: StateFlow<LibroListUiState> = _listState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun cargarLibros() {
        viewModelScope.launch {
            _listState.value = LibroListUiState.Loading
            repository.getLibros().fold(
                onSuccess = { libros -> _listState.value = LibroListUiState.Success(libros) },
                onFailure = { e -> _listState.value = LibroListUiState.Error(e.message ?: "Error desconocido") }
            )
        }
    }
}
