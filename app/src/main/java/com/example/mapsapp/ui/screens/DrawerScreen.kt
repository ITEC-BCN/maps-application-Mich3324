package com.example.mapsapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.DrawerItem
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(logout: () -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)// Estado inicial del cajón (cerrado)
    val scope = rememberCoroutineScope()// Alcance de corrutina para abrir/cerrar el cajón
    var selectedItemIndex by remember { mutableStateOf(0) }// Índice del ítem seleccionado

    ModalNavigationDrawer(
        gesturesEnabled = false, // Deshabilita gestos para abrir el drawer (solo se abre con botón)
        drawerContent = {//Contenido del cajón lateral
            ModalDrawerSheet {
                // Se listan los ítems definidos en DrawerItem
                DrawerItem.entries.forEachIndexed { index, drawerItem ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = drawerItem.icon,
                                contentDescription = drawerItem.text
                            )
                        },
                        label = { Text(text = drawerItem.text) },// Texto del ítem
                        selected = index == selectedItemIndex,// Ítem actualmente seleccionado
                        onClick = {
                            selectedItemIndex = index// Actualiza el ítem seleccionado
                            scope.launch { drawerState.close() }// Cierra el drawer con corrutina
                            navController.navigate(drawerItem.route)//Navega a la ruta correspondiente
                        }
                    )
                }
                // Área inferior con botón de logout
                Column(
                    Modifier.fillMaxSize().padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    IconButton(onClick = { logout() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            }
        },
        // Estado del cajón
        drawerState = drawerState
    ) {
        // Contenido principal de la pantalla
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mich Map App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                // Contenedor principal que renderiza el sistema de navegación interno
                InternalnavigationWraper(navController, Modifier.padding(innerPadding))
            }
        }



    }
}