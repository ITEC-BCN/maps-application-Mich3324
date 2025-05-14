package com.example.mapsapp.data

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mapsapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MySupabaseClient(var marcador: SupabaseClient) {


    var storage : Storage = marcador.storage


    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadImage(imageFile: ByteArray): String {
        val fechaHoraActual = LocalDateTime.now()
        val formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val imageName = storage.from("images").upload(path = "image_${fechaHoraActual.format(formato)}.png", data = imageFile)
        return buildImageUrl(imageFileName = imageName.path)
    }
    //funciones de base de datos

    fun buildImageUrl(imageFileName: String) = "${this.supabaseUrl}/storage/v1/object/public/images/${imageFileName}"

    suspend fun getMarcador (id:Int):Marcador{
        return marcador.from("Marcador").select {
            filter {
                eq("id",id)
            }
        }.decodeSingle<Marcador>()
    }
    suspend fun insertMarcador(marcador: Marcador){
       this.marcador.from("Marcador").insert(marcador)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateMarcador(id: Int, nombre: String, descripcion: String, imageName: String?, imageFile: ByteArray?){
        if(imageName != null && imageFile!= null){
            marcador.storage.from("images").delete(imageName)
            val newImageName = uploadImage(imageFile)
            marcador.from("Marcador").update({
                set("nombre", nombre)
                set("descripcion", descripcion)
                set("image_url", newImageName)
            }) { filter { eq("id", id) } }
        }
        else{
            marcador.from("Marcador").update({
                set("nombre", nombre)
                set("descripcion", descripcion)
            }) { filter { eq("id", id) } }
        }
    }
    suspend fun deleteMarcador(id: Int){
        marcador.from("Marcador").delete{ filter { eq("id", id) } }
    }

    suspend fun getAllMarcador():List<Marcador>{
        return marcador.from("Marcador").select().decodeList() //va la tabla demarcadpores lo selleciona y los tranforma en una lista
    }


}





