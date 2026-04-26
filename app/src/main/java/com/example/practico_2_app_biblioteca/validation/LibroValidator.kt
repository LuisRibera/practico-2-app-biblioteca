package com.example.practico_2_app_biblioteca.validation

object LibroValidator {

    fun validarNombre(nombre: String): String? =
        if (nombre.isBlank()) "El nombre es obligatorio" else null

    fun validarAutor(autor: String): String? =
        if (autor.isBlank()) "El autor es obligatorio" else null

    fun validarEditorial(editorial: String): String? =
        if (editorial.isBlank()) "La editorial es obligatoria" else null

    fun validarSinopsis(sinopsis: String): String? =
        if (sinopsis.isBlank()) "La sinopsis es obligatoria" else null

    fun validarIsbn(isbn: String): String? {
        if (isbn.isBlank()) return "El ISBN es obligatorio"
        // ISBN-10 o ISBN-13: solo dígitos, longitud 10 o 13
        if (!isbn.matches(Regex("\\d{10}|\\d{13}"))) return "El ISBN debe tener 10 o 13 dígitos numéricos"
        return null
    }

    fun validarImagen(url: String): String? {
        if (url.isBlank()) return "La URL de imagen es obligatoria"
        if (!url.startsWith("http://") && !url.startsWith("https://")) return "La URL debe comenzar con http:// o https://"
        return null
    }

    fun validarTodo(
        nombre: String,
        autor: String,
        editorial: String,
        sinopsis: String,
        isbn: String,
        imagen: String
    ): Boolean {
        return listOf(
            validarNombre(nombre),
            validarAutor(autor),
            validarEditorial(editorial),
            validarSinopsis(sinopsis),
            validarIsbn(isbn),
            validarImagen(imagen)
        ).all { it == null }
    }
}