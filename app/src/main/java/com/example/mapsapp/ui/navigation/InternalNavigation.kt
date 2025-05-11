package com.example.mapsapp.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mapsapp.ui.navigation.Destination

// aqui va la navegacion desde el mapa hasta cualquiera de las pantallas
//siguientes medianteun long click

@SuppressLint("WrongNavigateRouteType")
@RequiresApi(Build.VERSION_CODES.O)
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
                navigateToDetail = { id -> navController.navigate(Destination.List) },
                latd = lat,
                logd = lng
            )
        }
        composable<Destination.List> {
            List (navigateToDetail = { id -> navController.navigate(Destination.DetailMarker) })
        }

        composable<Destination.DetailMarker> { backStackEntry ->
            val id = backStackEntry.toRoute<Destination.DetailMarker>()
            DetailMarker(id.id) { navController.navigate(Destination.List) } // aqui ha de navegar a lapantalla de editar
        }

        /* composable<Destinacion.Pantalla2> { backStackEntry ->
            val pantalla2 = backStackEntry.toRoute<Destinacion.Pantalla2>()
            DetalleScreen(pantalla2.id) { navController.navigate(Destinacion.Pantalla1) }
        }*/
        /*composable<Destination.Detail> { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            MarkerDetailScreen(id = id)
        }*/
    }
}