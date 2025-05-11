package com.example.mapsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.data.Marcador
import com.example.mapsapp.viewmodels.MapsViewModel
import com.example.mapsapp.viewmodels.MapsViewModelFactory

@Composable
fun List(navigateToDetail:(Int)->Unit) {

    val factory = remember {
        MapsViewModelFactory(MyAppSingleton.database.client)
    }
    val myViewModel: MapsViewModel = viewModel(factory = factory)

    val marckerList by myViewModel.marckerList.observeAsState(emptyList())

    myViewModel.getAllMarkers()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(marckerList) { it ->
            MarkerItem(
                marcador = it,
                onClick = {click->navigateToDetail(click.id!!)}
            )
        }
    }
}

@Composable
fun MarkerItem(marcador: Marcador, onClick: (Marcador) -> Unit) {

    Card(
        border = BorderStroke(2.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick(marcador) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = marcador.nombre,
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
