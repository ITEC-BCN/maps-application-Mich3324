package com.example.mapsapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.R
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

    // Navegar si se registra correctamente
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

        // Contenido encima del fondo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Registro",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { viewModel.editEmail(it) },
                placeholder = { Text("Introduce tu email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(12.dp))

            TextField(
                value = password,
                onValueChange = { viewModel.editPassword(it) },
                placeholder = { Text("Introduce tu contraseña") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(25.dp))

            Button(
                onClick = { viewModel.signUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray, // Fondo del botón
                    contentColor = Color.White       // Color del texto
                ),
                contentPadding = PaddingValues()
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

        }
    }
}
