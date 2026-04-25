package com.example.practico_2_app_biblioteca.data.remote

import com.example.practico_2_app_biblioteca.data.model.GeneroDto
import com.example.practico_2_app_biblioteca.data.model.GeneroRequest
import com.example.practico_2_app_biblioteca.data.model.LibroDto
import com.example.practico_2_app_biblioteca.data.model.LibroGeneroRequest
import com.example.practico_2_app_biblioteca.data.model.LibroRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("libros")
    suspend fun getLibros(): List<LibroDto>

    @GET("libros/{id}")
    suspend fun getLibroById(@Path("id") id: Int): LibroDto

    @POST("libros")
    suspend fun crearLibro(@Body libro: LibroRequest): LibroDto

    @PUT("libros/{id}")
    suspend fun actualizarLibro(@Path("id") id: Int, @Body libro: LibroRequest): LibroDto

    @DELETE("libros/{id}")
    suspend fun eliminarLibro(@Path("id") id: Int): ResponseBody

    @GET("generos")
    suspend fun getGeneros(): List<GeneroDto>

    @POST("generos")
    suspend fun crearGenero(@Body genero: GeneroRequest): GeneroDto

    @DELETE("generos/{id}")
    suspend fun eliminarGenero(@Path("id") id: Int): ResponseBody

    @POST("libro-generos")
    suspend fun asignarGeneroALibro(@Body request: LibroGeneroRequest): ResponseBody
}
