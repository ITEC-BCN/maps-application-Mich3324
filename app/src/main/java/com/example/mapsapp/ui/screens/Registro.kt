package com.example.mapsapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.utils.AuthState
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.MapsViewModelFactory


@Composable
fun Registro(navToDrawer: () -> Unit) {
    val context = LocalContext.current
    val viewModel: AuthViewModel =
        viewModel(factory = MapsViewModelFactory(SharedPreferencesHelper(context)))

    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val showError by viewModel.showError.observeAsState(false)
    val authState by viewModel.authState.observeAsState()

    // Navegar si el usuario se ha registrado correctamente
    LaunchedEffect(authState) {
        if (authState == AuthState.Authenticated) {
            navToDrawer()
        }
    }

    if (showError && authState is AuthState.Error) {
        val errorMessage = (authState as AuthState.Error).message
        Toast.makeText(context, errorMessage ?: "Error en el registro", Toast.LENGTH_LONG).show()
        viewModel.errorMessageShowed()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { viewModel.editEmail(it) },
            placeholder = { Text("Introduce tu email") }
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { viewModel.editPassword(it) },
            placeholder = { Text("Introduce tu contraseña") }
        )
        Spacer(Modifier.height(25.dp))
        Button(onClick = {
            viewModel.signUp()
        }) {
            Text("Registrarse")
        }
    }
}
