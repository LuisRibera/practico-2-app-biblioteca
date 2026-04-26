package com.example.practico_2_app_biblioteca.validation

object GeneroValidator {
    fun validarNombre(nombre: String): String? =
        if (nombre.isBlank()) "El nombre del género es obligatorio" else null
}