package com.example.mapsapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Marcador(
    val id: Int? = null,
    val nombre: String,
    val descripcion: String,
    val latitud:Double,
    val longitud:Double,
    val image_url:String

)

