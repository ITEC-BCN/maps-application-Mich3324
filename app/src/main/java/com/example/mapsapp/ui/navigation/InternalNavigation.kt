package com.example.mapsapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.Destination

// aqui va la navegacion desde el mapa hasta cualquiera de las pantallas
//siguientes medianteun long click

@Composable
fun InternalnavigationWraper(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destination.Map
    ) {
        composable<Destination.Map> {
            MapsScreen()
        }
        composable<Destination.List> {
            List()
        }
        /*composable<Destination.Detail> { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            MarkerDetailScreen(id = id)
        }*/
    }
}