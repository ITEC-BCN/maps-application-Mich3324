package com.example.mapsapp.data.baseDeDatos

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mapsapp.BuildConfig
import com.example.mapsapp.data.model.Marcador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Clase que se encarga de gestionar todas las operaciones con la base de datos y almacenamiento de Supabase
class MySupabaseClient(var marcador: SupabaseClient) {

    // Creamos una instancia del sistema de almacenamiento de Supabase (para manejar imágenes, archivos, etc.)
    var storage: Storage = marcador.storage

    // Variables privadas para acceder a las constantes de Supabase, definidas en build.gradle y protegidas
    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    // Función para subir una imagen y devolver su URL pública
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadImage(imageFile: ByteArray): String {
        val fechaHoraActual = LocalDateTime.now()

        // Definimos un formato para el nombre del archivo: ejemplo "20250517_172301"
        val formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        // Subimos la imagen con un nombre generado a partir de la fecha
        val imageName = storage.from("images")
            .upload(path = "image_${fechaHoraActual.format(formato)}.png", data = imageFile)
        return buildImageUrl(imageFileName = imageName.path)
    }

    // Función auxiliar que construye la URL completa para acceder públicamente a la imagen subida
    fun buildImageUrl(imageFileName: String) =
        "${this.supabaseUrl}/storage/v1/object/public/images/${imageFileName}"

    // Recupera un único marcador desde la base de datos usando su ID
    suspend fun getMarcador(id: Int): Marcador {
        return marcador.from("Marcador").select {
            filter {
                eq("id", id)
            }
        }.decodeSingle<Marcador>() // Decodificamos el resultado a un objeto Marcador
    }


    suspend fun insertMarcador(marcador: Marcador) {
        this.marcador.from("Marcador").insert(marcador)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateMarcador(
        id: Int,
        nombre: String,
        descripcion: String,
        imageName: String?,
        imageFile: ByteArray?
    ) {

        // Si hay imagen nueva, primero borramos la anterior y subimos la nueva
        if (imageName != null && imageFile != null) {
            marcador.storage.from("images").delete(imageName)// Borra la imagen anterior
            val newImageName = uploadImage(imageFile) // Sube la nueva
            marcador.from("Marcador").update({
                set("nombre", nombre)
                set("descripcion", descripcion)
                set("image_url", newImageName)
            }) { filter { eq("id", id) } }
        } else {
            // Si no hay imagen, solo actualiza nombre y descripción
            marcador.from("Marcador").update({
                set("nombre", nombre)
                set("descripcion", descripcion)
            }) { filter { eq("id", id) } }
        }
    }

    suspend fun deleteMarcador(id: Int) {
        marcador.from("Marcador").delete { filter { eq("id", id) } }
    }

    //va la tabla demarcadpores lo selleciona y los tranforma en una lista
    suspend fun getAllMarcador(): List<Marcador> {
        return marcador.from("Marcador").select()
            .decodeList() //va la tabla demarcadpores lo selleciona y los tranforma en una lista
    }


}

/*
 ¿Qué hace esta clase?
Se comunica con Supabase (base de datos y almacenamiento).

Permite hacer las operaciones básicas:

Insertar, obtener, actualizar y eliminar marcadores.

Subir imágenes a Supabase Storage y generar su URL pública.

Es una especie de "puente" entre tu app y Supabase.

*/





