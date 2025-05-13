package com.example.mapsapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination{
    @Serializable
    object Permissions: Destination()

    @Serializable
    object Drawer : Destination()

    @Serializable
    object Map: Destination()


    @Serializable
    object List :Destination()

    @Serializable
    data class CrearMarcador(val lat:Double, val lng:Double):Destination()

    @Serializable
    data class Detail(val id: Int) : Destination()



}

