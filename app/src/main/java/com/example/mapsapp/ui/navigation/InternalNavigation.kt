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
            MapsScreen(
                { id ->
                    navController.navigate(Destination.Detail(id))
                },
                navigateToCreate = {lat, lang -> navController.navigate(Destination.CrearMarcador(lat,lang))}
            )
        }

        composable<Destination.CrearMarcador> { backStackEntry ->
            val lat = backStackEntry.arguments?.getDouble("lat") ?: 0.0
            val lng = backStackEntry.arguments?.getDouble("lng") ?: 0.0
            CreateMarker(
                navigateBack = {navController.navigate(Destination.Map) }, //
                latd = lat,
                logd = lng
            )
        }
        composable<Destination.List> {
            List(navigateToDetail = { id -> navController.navigate(Destination.Detail(id)) })
        }


        composable<Destination.Detail> { backStackEntry ->
            val id = backStackEntry.toRoute<Destination.Detail>()
            DetailMarker(modifier,id.id) { navController.navigate(Destination.List) } // des de aqui ha de navegar a lapantalla de editar
        }
    }
}