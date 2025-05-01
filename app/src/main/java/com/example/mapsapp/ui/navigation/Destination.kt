package com.example.mapsapp.ui.navigation

import kotlinx.serialization.Serializable


sealed class Destination{
    @Serializable
    object Permissions: Destination()

    @Serializable
    object Drawer : Destination()


    @Serializable
    object List :Destination()

    @Serializable
    object Map: Destination()



}

