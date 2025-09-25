package com.kote.obrio.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel,
    onOpenDetails: (String) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val bmpByUrl by viewModel.bmpByURL.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(items) { index, item ->
                    PokemonListItem(
                        pokemon = item,
                        bmpByUrl = bmpByUrl,
                        loadImage = viewModel::loadImage,
                        isFavorite = favorites.contains(item.id),
                        onFavorite = { viewModel.toggleFavorite(item.id) },
                        onDelete = { viewModel.delete(item.id) },
                        onClick = { onOpenDetails(item.name) }
                    )
                }

                item {
                    if (isLoading) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

//            LaunchedEffect(listState) {
//                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
//                    .collect { lastIndex ->
//                        if (lastIndex != null && lastIndex >= items.size - 5) {
//                            viewModel.loadNext()
//                        }
//                    }
//            }
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: UiPokemonBasic,
    bmpByUrl: MutableMap<String, Bitmap>,
    loadImage: (String) -> Unit,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){
            PokemonImage(pokemon.imageUrl, bmpByUrl, loadImage, modifier = Modifier.size(92.dp))
            Column(modifier = Modifier.weight(1f)){
                Text("${pokemon.id}")
                Text(pokemon.name)
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null)
            }
        }
    }
}

@Composable
private fun PokemonImage(
    url: String,
    bmpByUrl: MutableMap<String, Bitmap>,
    loadImage: (String) -> Unit,
    modifier : Modifier = Modifier
) {
    val bmp = bmpByUrl[url]
    if (bmp != null) {
        Image(bitmap = bmp.asImageBitmap(), contentDescription = null, modifier = modifier)
    } else {
        Timber.i("...Loading image ...")
        LaunchedEffect(url) { loadImage(url) }
    }
}
