package com.example.practico_2_app_biblioteca.data.repository

import com.example.practico_2_app_biblioteca.data.model.GeneroDto
import com.example.practico_2_app_biblioteca.data.model.GeneroRequest
import com.example.practico_2_app_biblioteca.data.remote.ApiService
import com.example.practico_2_app_biblioteca.data.remote.RetrofitClient

class GeneroRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getGeneros(): Result<List<GeneroDto>> = try {
        Result.success(apiService.getGeneros())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun crearGenero(request: GeneroRequest): Result<GeneroDto> = try {
        Result.success(apiService.crearGenero(request))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun eliminarGenero(id: Int): Result<Unit> = try {
        apiService.eliminarGenero(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
