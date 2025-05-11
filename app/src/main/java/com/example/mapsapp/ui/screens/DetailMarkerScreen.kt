package com.example.mapsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.R
import com.example.mapsapp.data.Marcador
import com.example.mapsapp.viewmodels.MapsViewModel
import com.example.mapsapp.viewmodels.MapsViewModelFactory

@Composable

fun DetailMarker(id: Int, function: () -> Unit) {
    val factory = remember {
        MapsViewModelFactory(MyAppSingleton.database.client)
    }
    val myViewModel: MapsViewModel = viewModel(factory = factory)

    val getMarker by myViewModel.selectedMarker.observeAsState()

    getMarker?.let { marker->

        LazyColumn(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                CardMarkerDetail(
                    marcador = marker
                )
            }
        }

    }

}

@Composable
fun CardMarkerDetail(marcador: Marcador) {

    Card(
        border = BorderStroke(2.dp, color = Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {

            AsyncImage(
                model = marcador.image_url, // Aquí usa character.imageUrl si quieres cargar la imagen dinámica
                contentDescription = marcador.nombre,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Imagen de placeholder
                error = painterResource(id = R.drawable.ic_launcher_foreground) // Imagen de error si la carga falla
            )
            Spacer(modifier = Modifier.width(20.dp))

            Column( modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,) {
                Text(
                    text = marcador.nombre,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar películas
                if (marcador.descripcion.isNotEmpty()) {
                    Text(
                        text = marcador.descripcion,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black

                    )
                }

                // Mostrar latitud de TV

                    Text(
                        text = "Series de TV: ${marcador.latitud}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )


                // Mostrar longitud
                Text(
                    text = "Series de TV: ${marcador.longitud}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )

            }

        }

    }
}