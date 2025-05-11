package com.example.mapsapp.ui.navigation

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable


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
    class CrearMarcador(val lat:Double, val lng:Double):Destination()



}

