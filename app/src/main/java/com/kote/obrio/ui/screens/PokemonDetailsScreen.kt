package com.kote.obrio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailsScreen(
    viewModel: PokemonViewModel,
    navigateBack: () -> Unit,
    name: String,
) {
    LaunchedEffect(name) {
        viewModel.loadPokemonDetails(name)
    }

    val pokemon by viewModel.selectedPokemon.collectAsState()
    val bmpByUrl by viewModel.bmpByURL.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        pokemon?.let { p ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,

                ) {
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "ID: ${p.id}",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "WEIGHT: ${p.weight}",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "HEIGHT: ${p.height}",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp
                    )

                    p.sprites.frontDefault?.let {
                        PokemonImage(
                            url = pokemon!!.sprites.frontDefault!!,
                            bmpByUrl =bmpByUrl,
                            loadImage = viewModel::loadImage,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: run {
                        Text(
                            text = "OPS",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pokemon not found")
            }
        }
    }
}