package com.example.practico_2_app_biblioteca.ui.screen.generos

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.practico_2_app_biblioteca.data.model.GeneroDto
import com.example.practico_2_app_biblioteca.ui.components.ConfirmDialog
import com.example.practico_2_app_biblioteca.ui.components.EmptyView
import com.example.practico_2_app_biblioteca.ui.components.ErrorView
import com.example.practico_2_app_biblioteca.ui.components.LoadingView
import com.example.practico_2_app_biblioteca.ui.navigation.Routes
import com.example.practico_2_app_biblioteca.viewmodel.GeneroListUiState
import com.example.practico_2_app_biblioteca.viewmodel.GeneroViewModel
import com.example.practico_2_app_biblioteca.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneroListScreen(navController: NavController) {
    val viewModel: GeneroViewModel = viewModel()
    val state by viewModel.listState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var generoAEliminar by remember { mutableStateOf<GeneroDto?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarGeneros()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is UiEvent.NavigateBack -> { }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Géneros") })
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
            FloatingActionButton(onClick = { navController.navigate(Routes.GENERO_CREATE) }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar género")
            }
        }
    ) { innerPadding ->
        when (val s = state) {
            is GeneroListUiState.Loading -> LoadingView(modifier = Modifier.padding(innerPadding))
            is GeneroListUiState.Error -> ErrorView(
                message = s.message,
                onRetry = { viewModel.cargarGeneros() },
                modifier = Modifier.padding(innerPadding)
            )
            is GeneroListUiState.Success -> {
                if (s.generos.isEmpty()) {
                    EmptyView(
                        message = "No hay géneros. ¡Agrega el primero!",
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        items(s.generos) { genero ->
                            GeneroItem(
                                genero = genero,
                                onDelete = { generoAEliminar = genero }
                            )
                        }
                    }
                }
            }
        }

        generoAEliminar?.let { genero ->
            ConfirmDialog(
                title = "Eliminar género",
                message = "¿Eliminar el género \"${genero.nombre}\"? Esta acción no se puede deshacer.",
                onConfirm = {
                    generoAEliminar = null
                    viewModel.eliminarGenero(genero.id)
                },
                onDismiss = { generoAEliminar = null }
            )
        }
    }
}

@Composable
private fun GeneroItem(genero: GeneroDto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = genero.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar género",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
