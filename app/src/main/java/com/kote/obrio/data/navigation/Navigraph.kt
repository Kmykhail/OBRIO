package com.kote.obrio.data.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kote.obrio.ui.screens.PokemonListScreen
import com.kote.obrio.ui.screens.PokemonViewModel

@Composable
fun Navigraph(
    navController: NavHostController,
    modifier: Modifier
) {

    NavHost(
        navController = navController,
        startDestination = "pokemonListScreen"
    ) {
        composable(
            route = "pokemonListScreen"
        ){
            PokemonListScreen(
                viewModel = hiltViewModel<PokemonViewModel>(),
                onOpenDetails = {}
            )
        }
    }
}