package com.example.mapsapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.screens.DrawerMenu
import com.example.mapsapp.ui.screens.Login
import com.example.mapsapp.ui.screens.PermissionsScreen
import com.example.mapsapp.ui.screens.Registro


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigationWraper(){
    val navController = rememberNavController()
    NavHost(navController,Destination.Permissions){
        composable<Destination.Permissions>{
            PermissionsScreen{navController.navigate(Destination.InicioDeSesion)}

        }
        composable<Destination.Drawer> {
            DrawerMenu{
                navController.navigate(Destination.InicioDeSesion) {
                    popUpTo<Destination.InicioDeSesion> { inclusive = true }
                }
            }
        }

        composable<Destination.InicioDeSesion> {
            Login({navController.navigate(Destination.RegistroUsuario)},{navController.navigate(Destination.Drawer)})

        }

        composable<Destination.RegistroUsuario> {
            Registro(){
                navController.navigate(Destination.Drawer)
            }
        }
    }


}