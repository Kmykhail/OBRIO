package com.kote.obrio.data.repository

import android.util.Log
import com.kote.obrio.data.api.PokemonApiService
import com.kote.obrio.data.model.Pokemon
import com.kote.obrio.data.model.PokemonListResponse
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val service: PokemonApiService
) {
    suspend fun getPokemonList(offset: Int, limit: Int): Result<PokemonListResponse> {
        return try {
            val resp = service.getPokemonList(offset, limit)
            Timber.i("Successfully: fetched pokemonList")
            Result.success(resp)
        } catch (t: Throwable) {
            Timber.i("Failed: fetch pokemonList")
            Result.failure(t)
        }
    }

    suspend fun getPokemonDetails(name: String): Result<Pokemon> {
        return try {
            val resp = service.getPokemonDetails(name)
            Timber.i("Successfully: fetched pokemon details")
            Result.success(resp)
        } catch (t: Throwable) {
            Timber.i("Failed: fetched pokemon details")
            Result.failure(t)
        }
    }
}