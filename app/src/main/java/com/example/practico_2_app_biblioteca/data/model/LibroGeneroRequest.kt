package com.example.practico_2_app_biblioteca.data.model

import com.google.gson.annotations.SerializedName

data class LibroGeneroRequest(
    @SerializedName("libro_id") val libroId: Int,
    @SerializedName("genero_id") val generoId: Int
)
