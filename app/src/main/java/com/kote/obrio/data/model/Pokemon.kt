package com.kote.obrio.data.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonBasic>
)

@Stable
data class PokemonBasic(
    val name: String,
    val url: String,
    val isDeleted: Boolean = false
) {
    fun getId(): Int { // imageId
        return url.split("/").dropLast(1).last().toInt()
    }
}

@Stable
data class Pokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites
)

@Stable
data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?
)