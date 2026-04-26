package com.example.practico_2_app_biblioteca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practico_2_app_biblioteca.data.model.GeneroDto
import com.example.practico_2_app_biblioteca.data.model.LibroDto
import com.example.practico_2_app_biblioteca.data.model.LibroRequest
import com.example.practico_2_app_biblioteca.data.repository.GeneroRepository
import com.example.practico_2_app_biblioteca.data.repository.LibroRepository
import com.example.practico_2_app_biblioteca.validation.LibroValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

sealed interface GenerosFormUiState {
    object Loading : GenerosFormUiState
    data class Success(val generos: List<GeneroDto>) : GenerosFormUiState
    data class Error(val message: String) : GenerosFormUiState
}

class LibroViewModel : ViewModel() {

    private val repository = LibroRepository()
    private val generoRepository = GeneroRepository()

    private val _listState = MutableStateFlow<LibroListUiState>(LibroListUiState.Loading)
    val listState: StateFlow<LibroListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<LibroDetailUiState>(LibroDetailUiState.Loading)
    val detailState: StateFlow<LibroDetailUiState> = _detailState.asStateFlow()

    private val _formNombre = MutableStateFlow("")
    val formNombre: StateFlow<String> = _formNombre.asStateFlow()

    private val _formAutor = MutableStateFlow("")
    val formAutor: StateFlow<String> = _formAutor.asStateFlow()

    private val _formEditorial = MutableStateFlow("")
    val formEditorial: StateFlow<String> = _formEditorial.asStateFlow()

    private val _formSinopsis = MutableStateFlow("")
    val formSinopsis: StateFlow<String> = _formSinopsis.asStateFlow()

    private val _formIsbn = MutableStateFlow("")
    val formIsbn: StateFlow<String> = _formIsbn.asStateFlow()

    private val _formImagen = MutableStateFlow("")
    val formImagen: StateFlow<String> = _formImagen.asStateFlow()

    private val _formGenerosSeleccionados = MutableStateFlow<Set<Int>>(emptySet())
    val formGenerosSeleccionados: StateFlow<Set<Int>> = _formGenerosSeleccionados.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _generosFormState = MutableStateFlow<GenerosFormUiState>(GenerosFormUiState.Loading)
    val generosFormState: StateFlow<GenerosFormUiState> = _generosFormState.asStateFlow()

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
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repository.eliminarLibro(id).fold(
                    onSuccess = {
                        _uiEvent.emit(UiEvent.ShowMessage("Libro eliminado exitosamente"))
                        _uiEvent.emit(UiEvent.NavigateBack)
                    },
                    onFailure = { e ->
                        _uiEvent.emit(UiEvent.ShowMessage("Error al eliminar el libro: ${e.message}"))
                    }
                )
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun setNombre(v: String) { _formNombre.value = v }
    fun setAutor(v: String) { _formAutor.value = v }
    fun setEditorial(v: String) { _formEditorial.value = v }
    fun setSinopsis(v: String) { _formSinopsis.value = v }
    fun setIsbn(v: String) { _formIsbn.value = v }
    fun setImagen(v: String) { _formImagen.value = v }

    fun toggleGenero(generoId: Int) {
        _formGenerosSeleccionados.update { current ->
            if (generoId in current) current - generoId else current + generoId
        }
    }

    fun limpiarFormulario() {
        _formNombre.value = ""
        _formAutor.value = ""
        _formEditorial.value = ""
        _formSinopsis.value = ""
        _formIsbn.value = ""
        _formImagen.value = ""
        _formGenerosSeleccionados.value = emptySet()
        _isSubmitting.value = false
    }

    fun cargarGenerosParaFormulario() {
        viewModelScope.launch {
            _generosFormState.value = GenerosFormUiState.Loading
            generoRepository.getGeneros().fold(
                onSuccess = { generos -> _generosFormState.value = GenerosFormUiState.Success(generos) },
                onFailure = { e -> _generosFormState.value = GenerosFormUiState.Error(e.message ?: "Error al cargar géneros") }
            )
        }
    }

    fun crearLibro() {
        if (_isSubmitting.value) return

        val nombre = _formNombre.value
        val autor = _formAutor.value
        val editorial = _formEditorial.value
        val sinopsis = _formSinopsis.value
        val isbn = _formIsbn.value
        val imagen = _formImagen.value

        if (!LibroValidator.validarTodo(nombre, autor, editorial, sinopsis, isbn, imagen)) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowMessage("Revisa los campos marcados en rojo")) }
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val request = LibroRequest(
                    nombre = nombre,
                    autor = autor,
                    editorial = editorial,
                    imagen = imagen,
                    sinopsis = sinopsis,
                    isbn = isbn,
                    calificacion = 0
                )

                repository.crearLibro(request).fold(
                    onSuccess = { libroCreado ->
                        // Asignar géneros seleccionados uno por uno
                        val generosIds = _formGenerosSeleccionados.value
                        for (generoId in generosIds) {
                            repository.asignarGenero(libroCreado.id, generoId)
                        }

                        _uiEvent.emit(UiEvent.ShowMessage("Libro creado exitosamente"))
                        _uiEvent.emit(UiEvent.NavigateBack)
                        limpiarFormulario()
                    },
                    onFailure = { e ->
                        _uiEvent.emit(UiEvent.ShowMessage("Error al crear el libro: ${e.message}"))
                    }
                )
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun cargarLibroParaEditar(id: Int) {
        viewModelScope.launch {
            _detailState.value = LibroDetailUiState.Loading
            repository.getLibroById(id).fold(
                onSuccess = { libro ->
                    _detailState.value = LibroDetailUiState.Success(libro)
                    // Poblar el formulario con los datos existentes
                    _formNombre.value = libro.nombre
                    _formAutor.value = libro.autor
                    _formEditorial.value = libro.editorial
                    _formSinopsis.value = libro.sinopsis
                    _formIsbn.value = libro.isbn
                    _formImagen.value = libro.imageUrl
                },
                onFailure = { e ->
                    _detailState.value = LibroDetailUiState.Error(e.message ?: "Error al cargar el libro")
                }
            )
        }
    }

    fun editarLibro(id: Int) {
        if (_isSubmitting.value) return

        val nombre = _formNombre.value
        val autor = _formAutor.value
        val editorial = _formEditorial.value
        val sinopsis = _formSinopsis.value
        val isbn = _formIsbn.value
        val imagen = _formImagen.value

        if (!LibroValidator.validarTodo(nombre, autor, editorial, sinopsis, isbn, imagen)) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowMessage("Revisa los campos marcados en rojo")) }
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val request = LibroRequest(
                    nombre = nombre,
                    autor = autor,
                    editorial = editorial,
                    imagen = imagen,
                    sinopsis = sinopsis,
                    isbn = isbn,
                    calificacion = 0
                )

                repository.actualizarLibro(id, request).fold(
                    onSuccess = {
                        _uiEvent.emit(UiEvent.ShowMessage("Libro actualizado exitosamente"))
                        _uiEvent.emit(UiEvent.NavigateBack)
                        limpiarFormulario()
                    },
                    onFailure = { e ->
                        _uiEvent.emit(UiEvent.ShowMessage("Error al actualizar el libro: ${e.message}"))
                    }
                )
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}