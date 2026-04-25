package com.example.practico_2_app_biblioteca.data.repository

import com.example.practico_2_app_biblioteca.data.model.LibroDto
import com.example.practico_2_app_biblioteca.data.model.LibroGeneroRequest
import com.example.practico_2_app_biblioteca.data.model.LibroRequest
import com.example.practico_2_app_biblioteca.data.remote.ApiService
import com.example.practico_2_app_biblioteca.data.remote.RetrofitClient

class LibroRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getLibros(): Result<List<LibroDto>> = try {
        Result.success(apiService.getLibros())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getLibroById(id: Int): Result<LibroDto> = try {
        Result.success(apiService.getLibroById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun crearLibro(request: LibroRequest): Result<LibroDto> = try {
        Result.success(apiService.crearLibro(request))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun actualizarLibro(id: Int, request: LibroRequest): Result<LibroDto> = try {
        Result.success(apiService.actualizarLibro(id, request))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun eliminarLibro(id: Int): Result<Unit> = try {
        apiService.eliminarLibro(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun asignarGenero(libroId: Int, generoId: Int): Result<Unit> = try {
        apiService.asignarGeneroALibro(LibroGeneroRequest(libroId, generoId))
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
