package com.example.practico_2_app_biblioteca.ui.screen.libros

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.example.practico_2_app_biblioteca.data.model.LibroDto
import com.example.practico_2_app_biblioteca.ui.components.EmptyView
import com.example.practico_2_app_biblioteca.ui.components.ErrorView
import com.example.practico_2_app_biblioteca.ui.components.LoadingView
import com.example.practico_2_app_biblioteca.ui.navigation.Routes
import com.example.practico_2_app_biblioteca.viewmodel.LibroListUiState
import com.example.practico_2_app_biblioteca.viewmodel.LibroViewModel
import com.example.practico_2_app_biblioteca.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibroListScreen(navController: NavController) {
    val viewModel: LibroViewModel = viewModel()
    val state by viewModel.listState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is UiEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarLibros()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Biblioteca") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.LIBRO_LIST,
                    onClick = {
                        navController.navigate(Routes.LIBRO_LIST) {
                            popUpTo(Routes.LIBRO_LIST) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Libros") },
                    label = { Text("Libros") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.GENERO_LIST,
                    onClick = {
                        navController.navigate(Routes.GENERO_LIST) {
                            popUpTo(Routes.LIBRO_LIST) { inclusive = false }
                        }
                    },
                    icon = { Icon(Icons.Filled.Label, contentDescription = "Géneros") },
                    label = { Text("Géneros") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.LIBRO_CREATE) }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar libro")
            }
        }
    ) { innerPadding ->
        when (val s = state) {
            is LibroListUiState.Loading -> LoadingView(modifier = Modifier.padding(innerPadding))
            is LibroListUiState.Error -> ErrorView(
                message = s.message,
                onRetry = { viewModel.cargarLibros() },
                modifier = Modifier.padding(innerPadding)
            )
            is LibroListUiState.Success -> {
                if (s.libros.isEmpty()) {
                    EmptyView(
                        message = "No hay libros. ¡Agrega el primero!",
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        //crea un item por cada libro, mostrando su imagen, titulo y autor
                        //cuando se clickea un libro, navega a su detalle
                        items(s.libros) { libro ->
                            LibroItem(libro = libro, onClick = {
                                navController.navigate(Routes.libroDetail(libro.id))
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibroItem(libro: LibroDto, onClick: () -> Unit) {
    //tarjeta de libro con imagen titulo y autor
    //detecta el click generado por el item y ejecuta la funcion onClick
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = libro.imageUrl,
                contentDescription = libro.nombre,
                modifier = Modifier
                    .width(72.dp)
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = libro.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = libro.autor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}