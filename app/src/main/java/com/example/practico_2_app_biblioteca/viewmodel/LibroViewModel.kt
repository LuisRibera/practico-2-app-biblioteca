package com.example.practico_2_app_biblioteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practico_2_app_biblioteca.data.model.LibroDto
import com.example.practico_2_app_biblioteca.data.repository.LibroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LibroListUiState {
    object Loading : LibroListUiState
    data class Success(val libros: List<LibroDto>) : LibroListUiState
    data class Error(val message: String) : LibroListUiState
}

sealed interface LibroDetailUiState {
    object Loading : LibroDetailUiState
    data class Success(val libro: LibroDto) : LibroDetailUiState
    data class Error(val message: String) : LibroDetailUiState
}

class LibroViewModel : ViewModel() {

    private val repository = LibroRepository()

    private val _listState = MutableStateFlow<LibroListUiState>(LibroListUiState.Loading)
    val listState: StateFlow<LibroListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<LibroDetailUiState>(LibroDetailUiState.Loading)
    val detailState: StateFlow<LibroDetailUiState> = _detailState.asStateFlow()

    fun cargarLibros() {
        viewModelScope.launch {
            _listState.value = LibroListUiState.Loading
            repository.getLibros().fold(
                onSuccess = { libros -> _listState.value = LibroListUiState.Success(libros) },
                onFailure = { e -> _listState.value = LibroListUiState.Error(e.message ?: "Error desconocido") }
            )
        }
    }

    fun cargarLibro(id: Int) {
        viewModelScope.launch {
            _detailState.value = LibroDetailUiState.Loading
            repository.getLibroById(id).fold(
                onSuccess = { libro -> _detailState.value = LibroDetailUiState.Success(libro) },
                onFailure = { e -> _detailState.value = LibroDetailUiState.Error(e.message ?: "Error desconocido") }
            )
        }
    }

    fun eliminarLibro(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarLibro(id)
                _listState.value = LibroListUiState.Loading //refrescar la lista tras eliminar
                cargarLibros()
            } catch (e: Exception) {
                _listState.value = LibroListUiState.Error(e.message ?: "Error al eliminar el libro")
            }
        }
    }
}
