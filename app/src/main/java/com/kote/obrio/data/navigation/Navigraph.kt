package com.kote.obrio.data.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.kote.obrio.ui.screens.PokemonDetailsScreen
import com.kote.obrio.ui.screens.PokemonListScreen
import com.kote.obrio.ui.screens.PokemonViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun Navigraph(
    navController: NavHostController,
    modifier: Modifier
) {

    NavHost(
        navController = navController,
        startDestination = "pokemon"
    ) {
        navigation(
            startDestination = "pokemonListScreen",
            route = "pokemon"
        ){
            composable(
                route = "pokemonListScreen"
            ){
                val parentEntry = remember { navController.getBackStackEntry("pokemon") }
                val vm: PokemonViewModel = hiltViewModel(parentEntry)

                PokemonListScreen(
                    viewModel = vm,
                    onOpenDetails = { name ->
                        navController.navigate("pokemonDetailsScreen/${name}")
                    }
                )
            }

            composable(
                route = "pokemonDetailsScreen/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember { navController.getBackStackEntry("pokemon") }
                val vm: PokemonViewModel = hiltViewModel(parentEntry)
                val name = backStackEntry.arguments?.getString("name") ?: return@composable

                PokemonDetailsScreen(
                    viewModel = vm,
                    name = name,
                    navigateBack = { navController.popBackStack()}
                )
            }
        }
    }
}