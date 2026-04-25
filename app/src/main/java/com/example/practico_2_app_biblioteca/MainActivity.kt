package com.example.practico_2_app_biblioteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.practico_2_app_biblioteca.ui.navigation.AppNavHost
import com.example.practico_2_app_biblioteca.ui.theme.Practico_2_app_bibliotecaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practico_2_app_bibliotecaTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
