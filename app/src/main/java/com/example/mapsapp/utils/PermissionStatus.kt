package com.example.mapsapp.utils


// representa els possibles estats d’un permís.

sealed class PermissionStatus {
    object Granted : PermissionStatus()
    object Denied : PermissionStatus()
    object PermanentlyDenied : PermissionStatus()
}
