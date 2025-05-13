package com.example.mapsapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.viewmodels.MapsViewModel
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapsScreen(navigateToDetail: (Int) -> Unit,navigateToCreate: (Double, Double) -> Unit) {

    val viewModel: MapsViewModel = viewModel<MapsViewModel>()
    val markerList by viewModel.marckerList.observeAsState()
    val markerPositions by viewModel.markerPositions.collectAsState()
    val cameraPositionState = viewModel.initialCameraPosition
    LaunchedEffect(markerList) {
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

