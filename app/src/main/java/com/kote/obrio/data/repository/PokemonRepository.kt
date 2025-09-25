package com.kote.obrio.data.repository

import com.kote.obrio.data.api.PokemonApiService
import com.kote.obrio.data.model.Pokemon
import com.kote.obrio.data.model.PokemonListResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val service: PokemonApiService
) {
    suspend fun getPokemonList(offset: Int, limit: Int): Result<PokemonListResponse> {
        return try {
            val resp = service.getPokemonList(offset, limit)
            Result.success(resp)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    suspend fun getPokemonDetails(name: String): Result<Pokemon> {
        return try {
            val resp = service.getPokemonDetails(name)
            Result.success(resp)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}