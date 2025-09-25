package com.kote.obrio.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.obrio.data.model.PokemonBasic
import com.kote.obrio.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class UiPokemonBasic(
    val id: Int,
    val name: String,
    val imageUrl: String,
)

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val apiRepository: PokemonRepository
): ViewModel() {
    private val _items = MutableStateFlow<List<UiPokemonBasic>>(emptyList())
    val items: StateFlow<List<UiPokemonBasic>> = _items.asStateFlow()

    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var offset = 0
    private var end = false
    private val pageSize = 20

    init {
        loadNext()
    }

    fun loadNext() {
        if (_isLoading.value || end) return
        _isLoading.value = true

        viewModelScope.launch {
            val res = apiRepository.getPokemonList(offset, pageSize)
            when {
                res.isSuccess -> {
                    val body = res.getOrNull()!!
                    val basics = body.results.map {  pBasic ->
                        val id = pBasic.getId()
                        UiPokemonBasic(
                            id = id,
                            name = pBasic.name,
                            imageUrl = getImageUrl(pBasic)
                        )
                    }
                    _items.value = _items.value + basics
                    offset += pageSize
                    if (body.next == null) end = true
                    _isLoading.value = false
                }
                res.isFailure -> {
                    _isLoading.value = false
                    Timber.e("Failed to get PokemonListResponse")
                }
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _items.value = _items.value.filterNot { it.id == id }
            _favorites.value = _favorites.value.filterNot { it == id }.toSet()
        }
    }

    fun toggleFavorite(id: Int) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _favorites.value = current
    }

    private fun getImageUrl(basic: PokemonBasic): String {
        return try {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${basic.getId()}.png"
        } catch (e: Exception) {
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png"
        }
    }
}