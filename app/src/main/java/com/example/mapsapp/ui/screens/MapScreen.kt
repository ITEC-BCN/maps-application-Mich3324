package com.example.mapsapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.viewmodels.MapsViewModel
import com.example.mapsapp.viewmodels.MapsViewModelFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapsScreen(navigateToNext: (Double,Double) -> Unit) {
    val factory = remember {
        MapsViewModelFactory(MyAppSingleton.database.client)
    }
    val viewModel: MapsViewModel = viewModel(factory = factory)

    val markerPositions by viewModel.markerPositions.collectAsState()
    val cameraPositionState = viewModel.initialCameraPosition

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = {
                latLng ->
            viewModel.setLatLng(latLng.latitude, latLng.longitude)
            navigateToNext(latLng.latitude, latLng.longitude)

        }
    ) {
        markerPositions.forEachIndexed { index, position ->
            Marker(
                state = MarkerState(position = position),
                title = "Marcador #${index + 1}",
                snippet = "Tócalo para eliminar",
                onClick = {
                    viewModel.removeMarker(position)
                    true
                }
            )
        }
    }
}