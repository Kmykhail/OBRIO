package com.kote.obrio.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.LruCache
import androidx.compose.ui.geometry.Offset
import androidx.core.util.lruCache
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kote.obrio.data.cache.ImageMemoryCache
import com.kote.obrio.data.model.Pokemon
import com.kote.obrio.data.model.PokemonBasic
import com.kote.obrio.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL
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

    private val _bmpByUrl = MutableStateFlow<Map<String, Bitmap>>(mutableMapOf())
    val bmpByURL = _bmpByUrl.asStateFlow()

    private val _selectedPokemon = MutableStateFlow<Pokemon?>(null)
    val selectedPokemon = _selectedPokemon.asStateFlow()

    private var offset = 0
    private var end = false
    private val pageSize = 20

    init {
            loadNext()
    }

    fun loadPokemonDetails(name: String) {
        viewModelScope.launch {
            val res = apiRepository.getPokemonDetails(name)
            when {
                res.isSuccess -> {
                    _selectedPokemon.value = res.getOrNull()!!
                    println(">>>>>>>>${_selectedPokemon.value}, ${_items.value.size}")
                }
                res.isFailure -> {
                    Timber.e("Failed to get specific pokemon")
                }
            }
        }
    }

    fun loadNext() {
        if (_isLoading.value || end) return
        _isLoading.value = true

        Timber.i(">>>>Offset: $offset")
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
            Timber.i("UiPokemonBasic items size: ${_items.value.size}")
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

    fun loadImage(url:String) {
        if (ImageMemoryCache.get(url) == null) {
            viewModelScope.launch {
                val bmp = downloadBitmap(url)
                bmp?.let {
                    ImageMemoryCache.put(url, it)
                    _bmpByUrl.update { curMap ->
                        (curMap + (url to it)) as MutableMap<String, Bitmap>
                    }
                }
            }
        }
    }

    private suspend fun downloadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO){
        try {
            val stream = URL(url).openStream()
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            Timber.e("Failed decodeStream by url: $url")
            null
        }
    }
}
