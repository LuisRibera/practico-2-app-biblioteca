package com.example.practico_2_app_biblioteca.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.practico_2_app_biblioteca.ui.screen.libros.LibroListScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIBRO_LIST,
        modifier = modifier
    ) {
        composable(Routes.LIBRO_LIST) {
            LibroListScreen(navController = navController)
        }
        composable(
            route = Routes.LIBRO_DETAIL,
            arguments = listOf(navArgument("libroId") { type = NavType.IntType })
        ) {
            Text("Próximamente — Detalle")
        }
        composable(Routes.LIBRO_CREATE) {
            Text("Próximamente — Crear libro")
        }
        composable(
            route = Routes.LIBRO_EDIT,
            arguments = listOf(navArgument("libroId") { type = NavType.IntType })
        ) {
            Text("Próximamente — Editar libro")
        }
        composable(Routes.GENERO_LIST) {
            Text("Próximamente — Géneros")
        }
        composable(Routes.GENERO_CREATE) {
            Text("Próximamente — Crear género")
        }
    }
}
