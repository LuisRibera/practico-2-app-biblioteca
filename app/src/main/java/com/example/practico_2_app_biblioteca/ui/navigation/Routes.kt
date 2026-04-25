package com.example.practico_2_app_biblioteca.ui.navigation

object Routes {
    const val LIBRO_LIST = "libro_list"
    const val LIBRO_DETAIL = "libro_detail/{libroId}"
    const val LIBRO_CREATE = "libro_create"
    const val LIBRO_EDIT = "libro_edit/{libroId}"
    const val GENERO_LIST = "genero_list"
    const val GENERO_CREATE = "genero_create"

    fun libroDetail(id: Int) = "libro_detail/$id"
    fun libroEdit(id: Int) = "libro_edit/$id"
}
