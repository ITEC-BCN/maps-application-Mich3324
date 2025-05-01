package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.screens.DrawerMenu
import com.example.mapsapp.ui.screens.PermissionsScreen


@Composable
fun MainnavigationWraper(){
    val navController = rememberNavController()
    NavHost(navController,Destination.Permissions){
        composable<Destination.Permissions>{
            PermissionsScreen{navController.navigate(Destination.Drawer)}

        }
        composable<Destination.Drawer> {
            DrawerMenu(navController)
        }
    }


}