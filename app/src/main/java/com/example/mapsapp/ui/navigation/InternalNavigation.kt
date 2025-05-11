package com.example.mapsapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            MapsScreen() { lat,lng ->
                navController.navigate(Destination.CrearMarcador(lat = lat, lng= lng))
            }
        }

        composable<Destination.CrearMarcador> { backStackEntry ->
            val lat = backStackEntry.arguments?.getDouble("lat") ?: 0.0
            val lng = backStackEntry.arguments?.getDouble("lng") ?: 0.0
            CreateMarker(
                navigateToDetail = { id -> navController.navigate("detail/$id") },
                latitud = lat,
                longitud = lng
            )
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