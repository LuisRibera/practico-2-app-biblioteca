package com.example.practico_2_app_biblioteca.ui.screen.generos

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practico_2_app_biblioteca.validation.GeneroValidator
import com.example.practico_2_app_biblioteca.viewmodel.GeneroViewModel
import com.example.practico_2_app_biblioteca.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneroFormScreen(navController: NavController) {
    val viewModel: GeneroViewModel = viewModel()
    val context = LocalContext.current

    val nombre by viewModel.formNombre.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val errorNombre = GeneroValidator.validarNombre(nombre)

    LaunchedEffect(Unit) {
        viewModel.limpiarFormulario()
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
                title = { Text("Nuevo género") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.setNombre(it) },
                label = { Text("Nombre del género") },
                isError = nombre.isNotEmpty() && errorNombre != null,
                supportingText = { if (nombre.isNotEmpty() && errorNombre != null) Text(errorNombre) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.crearGenero() },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar género")
                }
            }
        }
    }
}
