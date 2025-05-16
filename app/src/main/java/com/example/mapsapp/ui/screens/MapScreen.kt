package com.example.mapsapp.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mapsapp.data.Marcador
import com.example.mapsapp.viewmodels.MapsViewModel
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

/*@Composable
fun MapsScreen(navigateToDetail: (Int) -> Unit,navigateToCreate: (Double, Double) -> Unit) {

    val viewModel: MapsViewModel = viewModel<MapsViewModel>()
    val markerList by viewModel.marckerList.observeAsState()
    val cameraPositionState = viewModel.initialCameraPosition
    val showLoading: Boolean by viewModel.showloading.observeAsState(true)


    //para que se ejecute cada vez que se entra al Mapa
    LaunchedEffect(Unit) {
        viewModel.getAllMarkers()
    }

    if (showLoading) {
        viewModel.getAllMarkers()
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }else {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                viewModel.setLatLng(latLng.latitude, latLng.longitude)
                navigateToCreate(latLng.latitude, latLng.longitude)

            }
        ) {
            markerList?.forEach { marcador ->
                Marker(
                    state = MarkerState(position = LatLng(marcador.latitud, marcador.longitud)),
                    title = marcador.nombre,
                    snippet = marcador.descripcion,
                    onClick = {
                        navigateToDetail(marcador.id ?: 0)
                        true
                    }
                )
            }
        }
    }
}*/

@Composable
fun MapsScreen(
    navigateToDetail: (Int) -> Unit,
    navigateToCreate: (Double, Double) -> Unit
) {
    val viewModel: MapsViewModel = viewModel()
    val markerList by viewModel.marckerList.observeAsState()
    val selectedMarker by viewModel.selectedMarker.observeAsState()
    val showLoading by viewModel.showloading.observeAsState(initial = true)
    val cameraPositionState = viewModel.initialCameraPosition

    LaunchedEffect(Unit) {
        viewModel.getAllMarkers()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showLoading) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { latLng ->
                    viewModel.setLatLng(latLng.latitude, latLng.longitude)
                    navigateToCreate(latLng.latitude, latLng.longitude)
                }
            ) {
                markerList?.forEach { marcador ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(marcador.latitud, marcador.longitud)
                        ),
                        title = marcador.nombre,
                        snippet = marcador.descripcion,
                        onClick = {
                            viewModel.selectMarker(marcador)
                            true
                        }
                    )
                }
            }
        }

        // Mostrar Card solo si hay un marcador seleccionado
        selectedMarker?.let { marcador ->
            FloatingMarkerCard(
                marcador = marcador,
                onClose = {
                    viewModel.clearSelectedMarcador()
                    viewModel.centerMapOnAllMarkers()
                },
                navigateToDetail = { navigateToDetail(marcador.id ?: 0) }
            )
        }
    }
}
@Composable
fun FloatingMarkerCard(
    marcador: Marcador,
    onClose: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray .copy(alpha = 0.9f) // blanco translúcido
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = marcador.nombre,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            marcador.image_url?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Imagen del marcador",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = marcador.descripcion ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editar marcador",
                    modifier = Modifier.clickable { navigateToDetail(marcador.id ?: 0) },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }
        }
    }
}
