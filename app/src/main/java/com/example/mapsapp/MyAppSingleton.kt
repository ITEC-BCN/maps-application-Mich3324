package com.example.mapsapp

import android.app.Application
import com.example.mapsapp.data.MySupabaseClient
import com.example.mapsapp.data.SupabaseAuthentication
// y un solo singleton para los dos
class MyAppSingleton: Application() {
    companion object {
        lateinit var database: MySupabaseClient
        lateinit var auth: SupabaseAuthentication
    }
    override fun onCreate() {
        super.onCreate()
        auth = SupabaseAuthentication()
        database = MySupabaseClient(auth.getSupabaseClient()) //aqui se comunica con la base de datos
    }
}
