package com.example.practico_2_app_biblioteca.data.model

import com.google.gson.annotations.SerializedName

data class GeneroDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)
