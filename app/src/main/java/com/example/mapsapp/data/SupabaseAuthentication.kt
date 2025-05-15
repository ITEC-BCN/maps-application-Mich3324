package com.example.mapsapp.data

import com.example.mapsapp.BuildConfig
import com.example.mapsapp.utils.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient

class SupabaseAuthentication {

    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

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


    fun retrieveCurrentSession(): UserSession? {
        val session = supabase.auth.currentSessionOrNull()
        return session
    }

    fun refreshSession(): AuthState {
        try {
            supabase.auth.currentSessionOrNull()
            return AuthState.Authenticated
        } catch (e: Exception) {
            return AuthState.Error(e.localizedMessage)
        }
    }

    fun getSupabaseClient(): SupabaseClient {
        return supabase
    }
}