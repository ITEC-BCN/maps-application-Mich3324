package com.example.mapsapp.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MapsViewModel
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapsScreen(navigateToDetail: (Int) -> Unit,navigateToCreate: (Double, Double) -> Unit) {

    val viewModel: MapsViewModel = viewModel<MapsViewModel>()
    val markerList by viewModel.marckerList.observeAsState()
    val cameraPositionState = viewModel.initialCameraPosition

    //para que se ejecute cada vez que se entra al Mapa
    LaunchedEffect(Unit) {
        /*delay(1000)*/
        viewModel.getAllMarkers()
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = {
                latLng ->
            viewModel.setLatLng(latLng.latitude, latLng.longitude)
            navigateToCreate(latLng.latitude, latLng.longitude)

        }
    ) {
        markerList?.forEach{ marcador->
            Marker(
                state = MarkerState(position = LatLng(marcador.latitud,marcador.longitud)),
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


