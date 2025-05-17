package com.example.mapsapp.data.baseDeDatos

import com.example.mapsapp.BuildConfig
import com.example.mapsapp.utils.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient

// Clase encargada de la autenticación de usuarios usando Supabase
class SupabaseAuthentication {


    // Obtenemos las credenciales desde las variables definidas en build.gradle (protegidas)
    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY


    // Creamos el cliente Supabase, instalando los módulos necesarios: Auth, Postgrest y Storage
    private val supabase = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    )
    {
        install(Auth) {
            autoLoadFromStorage = true
        }
        install(io.github.jan.supabase.postgrest.Postgrest)
        install(io.github.jan.supabase.storage.Storage)

    }

    // Función para registrar un usuario nuevo con email y contraseña
    suspend fun RegistreWithEmail(emailValue: String, passwordValue: String): AuthState {
        try {
            supabase.auth.signUpWith(Email) {
                email = emailValue
                password = passwordValue
            }
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }

    // Función para iniciar sesión con email y contraseña
    suspend fun signInWithEmail(emailValue: String, passwordValue: String): AuthState {
        try {
            supabase.auth.signInWith(Email) {
                email = emailValue
                password = passwordValue
            }
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }

    // Recupera la sesión actual (si existe) o devuelve null
    fun retrieveCurrentSession(): UserSession? {
        val session = supabase.auth.currentSessionOrNull()
        return session
    }

    // Intenta refrescar o validar si la sesión sigue activa
    fun refreshSession(): AuthState {
        try {
            supabase.auth.currentSessionOrNull()
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }

    // Devuelve el cliente Supabase para usarlo desde otras clases como MySupabaseClient
    fun getSupabaseClient(): SupabaseClient {
        return supabase
    }
}

/*
¿Qué hace esta clase?
Centraliza toda la lógica de registro, inicio de sesión y gestión de sesión.

Se asegura de que el cliente de Supabase esté bien configurado.

Devuelve un AuthState que se puede usar en la UI para saber si el usuario está autenticado o no.

Se usa en MyAppSingleton para pasar el cliente ya configurado a la clase MySupabaseClient. */