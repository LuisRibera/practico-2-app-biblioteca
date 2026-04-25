package com.example.practico_2_app_biblioteca.data.model

import com.google.gson.annotations.SerializedName

data class GeneroRequest(
    @SerializedName("nombre") val nombre: String
)
