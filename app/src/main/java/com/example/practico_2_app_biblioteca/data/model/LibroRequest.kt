package com.example.practico_2_app_biblioteca.data.model

import com.google.gson.annotations.SerializedName

data class LibroRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("autor") val autor: String,
    @SerializedName("editorial") val editorial: String,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("sinopsis") val sinopsis: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("calificacion") val calificacion: Int = 0
)
