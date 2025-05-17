package com.example.mapsapp

import android.app.Application
import com.example.mapsapp.data.baseDeDatos.MySupabaseClient
import com.example.mapsapp.data.baseDeDatos.SupabaseAuthentication
// y un solo singleton para los dos

// Definimos una clase que extiende de Application, lo que nos permite crear variables globales
class MyAppSingleton: Application() {
    // Objeto companion: se comporta como un singleton para acceder a variables de forma global desde cualquier parte de la app
    companion object {
        lateinit var database: MySupabaseClient
        lateinit var auth: SupabaseAuthentication // // Variable que contendrá la instancia encargada de gestionar la autenticación de usuarios
    }
    // Metodo que se ejecuta cuando se inicia la app (antes de cualquier actividad o pantalla)
    override fun onCreate() {
        super.onCreate()
        // Inicializamos el sistema de autenticación (creamos la instancia)
        auth = SupabaseAuthentication()
        // Inicializamos el cliente de base de datos, pasándole el cliente ya autenticado
        database = MySupabaseClient(auth.getSupabaseClient()) //aqui se comunica con la base de datos
    }
}
