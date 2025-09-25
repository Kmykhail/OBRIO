package com.kote.obrio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel,
    onOpenDetails: (String) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                itemsIndexed(items) { index, item ->
                    PokemonListItem(
                        pokemon = item,
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

//            // infinite scroll trigger: when visible last index >= size - 5 -> load next
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastIndex ->
                        if (lastIndex != null && lastIndex >= items.size - 5) {
                            viewModel.loadNext()
                        }
                    }
            }
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: UiPokemonBasic,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {

}
