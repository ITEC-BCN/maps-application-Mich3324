package com.example.mapsapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.R
import com.example.mapsapp.utils.AuthState
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.MapsViewModelFactory

@Composable
fun Login(navToRegister: () -> Unit, navToDrawer: () -> Unit) {
    val context = LocalContext.current
    val viewModel: AuthViewModel =
        viewModel(factory =  MapsViewModelFactory(SharedPreferencesHelper(context)))

    val authState by viewModel.authState.observeAsState()
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val showError by viewModel.showError.observeAsState(false)

    LaunchedEffect(authState) {
        if (authState == AuthState.Authenticated) {
            navToDrawer()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Imagen de fondo (pon tu propia imagen en res/drawable)
        Image(
            painter = painterResource(id = R.drawable.fondo), // Cambia 'fondo' por tu imagen
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (authState == AuthState.Authenticated) {
            navToDrawer()
        } else {
            if (showError) {
                val errorMessage = (authState as AuthState.Error).message
                if (errorMessage.contains("invalid_credentials")) {
                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "An error has ocurred", Toast.LENGTH_LONG).show()
                }
                viewModel.errorMessageShowed()
            }

        }
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Acceso", fontSize = 20.sp)
            Spacer(Modifier.height(0.dp))
            TextField(
                value = email,
                onValueChange = { viewModel.editEmail(it) },
                placeholder = { Text("Introduce tu email") }
            )
            TextField(
                value = password,
                onValueChange = { viewModel.editPassword(it) },
                placeholder = { Text("Introduce tu contraseña") }
            )
            Spacer(Modifier.height(25.dp))
            Button(onClick = { viewModel.signIn() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray, // Fondo del botón
                    contentColor = Color.White       // Color del texto
                ),
                contentPadding = PaddingValues()

            )

            {
                Text("Iniciar sesión")
            }
            Spacer(Modifier.height(25.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(Color(0xAA000000), shape = RoundedCornerShape(8.dp)) // Fondo negro translúcido
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta?",
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(onClick = { navToRegister() }) {
                    Text(
                        "Regístrate aquí",
                        color = Color(0xFFBB86FC) // O el color primario que uses
                    )
                }
            }
        }
    }
}