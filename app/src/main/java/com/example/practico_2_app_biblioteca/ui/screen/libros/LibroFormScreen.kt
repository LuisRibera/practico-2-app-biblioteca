package com.example.practico_2_app_biblioteca.ui.screen.libros

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.practico_2_app_biblioteca.ui.components.LoadingView
import com.example.practico_2_app_biblioteca.validation.LibroValidator
import com.example.practico_2_app_biblioteca.viewmodel.GenerosFormUiState
import com.example.practico_2_app_biblioteca.viewmodel.LibroDetailUiState
import com.example.practico_2_app_biblioteca.viewmodel.LibroViewModel
import com.example.practico_2_app_biblioteca.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibroFormScreen(
    libroId: Int? = null,
    navController: NavController,
    viewModel: LibroViewModel
) {
    val context = LocalContext.current

    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    val nombre by viewModel.formNombre.collectAsStateWithLifecycle()
    val autor by viewModel.formAutor.collectAsStateWithLifecycle()
    val editorial by viewModel.formEditorial.collectAsStateWithLifecycle()
    val isbn by viewModel.formIsbn.collectAsStateWithLifecycle()
    val imagen by viewModel.formImagen.collectAsStateWithLifecycle()
    val sinopsis by viewModel.formSinopsis.collectAsStateWithLifecycle()

    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val generosFormState by viewModel.generosFormState.collectAsStateWithLifecycle()
    val generosSeleccionados by viewModel.formGenerosSeleccionados.collectAsStateWithLifecycle()

    val errorNombre = LibroValidator.validarNombre(nombre)
    val errorAutor = LibroValidator.validarAutor(autor)
    val errorEditorial = LibroValidator.validarEditorial(editorial)
    val errorIsbn = LibroValidator.validarIsbn(isbn)
    val errorImagen = LibroValidator.validarImagen(imagen)
    val errorSinopsis = LibroValidator.validarSinopsis(sinopsis)

    // Inicialización dependiente de si es CREAR o EDITAR
    LaunchedEffect(libroId) {
        if (libroId != null) {
            viewModel.cargarLibroParaEditar(libroId)
            // No cargamos géneros al editar según las instrucciones
        } else {
            viewModel.limpiarFormulario()
            viewModel.cargarGenerosParaFormulario()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navController.popBackStack()
                is UiEvent.ShowMessage -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (libroId != null) "Editar libro" else "Nuevo libro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        //mostrar spinner global solo si estamos cargando los datos para editar
        if (libroId != null && detailState is LibroDetailUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.setNombre(it) },
                label = { Text("Nombre") },
                isError = nombre.isNotEmpty() && errorNombre != null,
                supportingText = { if (nombre.isNotEmpty() && errorNombre != null) Text(errorNombre) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = autor,
                onValueChange = { viewModel.setAutor(it) },
                label = { Text("Autor") },
                isError = autor.isNotEmpty() && errorAutor != null,
                supportingText = { if (autor.isNotEmpty() && errorAutor != null) Text(errorAutor) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = editorial,
                onValueChange = { viewModel.setEditorial(it) },
                label = { Text("Editorial") },
                isError = editorial.isNotEmpty() && errorEditorial != null,
                supportingText = { if (editorial.isNotEmpty() && errorEditorial != null) Text(errorEditorial) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = isbn,
                onValueChange = { viewModel.setIsbn(it) },
                label = { Text("ISBN (10 o 13 dígitos)") },
                isError = isbn.isNotEmpty() && errorIsbn != null,
                supportingText = { if (isbn.isNotEmpty() && errorIsbn != null) Text(errorIsbn) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = imagen,
                onValueChange = { viewModel.setImagen(it) },
                label = { Text("URL de la imagen") },
                isError = imagen.isNotEmpty() && errorImagen != null,
                supportingText = { if (imagen.isNotEmpty() && errorImagen != null) Text(errorImagen) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            OutlinedTextField(
                value = sinopsis,
                onValueChange = { viewModel.setSinopsis(it) },
                label = { Text("Sinopsis") },
                isError = sinopsis.isNotEmpty() && errorSinopsis != null,
                supportingText = { if (sinopsis.isNotEmpty() && errorSinopsis != null) Text(errorSinopsis) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
                maxLines = 5
            )

            //mostrar generos solo al crear un libro
            if (libroId == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Géneros", style = MaterialTheme.typography.titleMedium)

                when (val state = generosFormState) {
                    is GenerosFormUiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    is GenerosFormUiState.Error -> Text("Error al cargar géneros", color = MaterialTheme.colorScheme.error)
                    is GenerosFormUiState.Success -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            items(state.generos) { genero ->
                                FilterChip(
                                    selected = genero.id in generosSeleccionados,
                                    onClick = { viewModel.toggleGenero(genero.id) },
                                    label = { Text(genero.nombre) }
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nota: Los géneros ya asignados no se pueden modificar desde aquí.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (libroId != null) viewModel.editarLibro(libroId)
                    else viewModel.crearLibro()
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (libroId != null) "Actualizar libro" else "Guardar libro")
                }
            }
        }
    }
}