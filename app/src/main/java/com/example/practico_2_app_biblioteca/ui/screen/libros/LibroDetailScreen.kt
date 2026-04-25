package com.example.practico_2_app_biblioteca.ui.screen.libros

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.practico_2_app_biblioteca.ui.components.ErrorView
import com.example.practico_2_app_biblioteca.ui.components.LoadingView
import com.example.practico_2_app_biblioteca.viewmodel.LibroViewModel
import com.example.practico_2_app_biblioteca.viewmodel.LibroDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibroDetailScreen(libroId: Int, navController: NavController, viewModel: LibroViewModel) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(libroId) {
        viewModel.cargarLibro(libroId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Libro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (detailState) {
                is LibroDetailUiState.Loading -> LoadingView()
                is LibroDetailUiState.Error -> ErrorView(
                    message = (detailState as LibroDetailUiState.Error).message,
                    onRetry = { viewModel.cargarLibro(libroId) }
                )
                is LibroDetailUiState.Success -> {
                    val libro = (detailState as LibroDetailUiState.Success).libro
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = libro.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(libro.nombre, style = MaterialTheme.typography.headlineMedium)
                        Text("Autor: ${libro.autor}")
                        Text("Editorial: ${libro.editorial}")
                        Text("ISBN: ${libro.isbn}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sinopsis", style = MaterialTheme.typography.titleMedium)
                        Text(libro.sinopsis)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { navController.navigate("libroEdit/$libroId") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.eliminarLibro(libroId)
                                    navController.navigate("libro_list") {
                                        popUpTo("libro_list") { inclusive = true }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
