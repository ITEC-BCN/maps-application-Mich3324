package com.example.mapsapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.launch


/*@Composable
fun List(navigateToDetail: (Int) -> Unit) {
    val myViewModel: MapsViewModel = viewModel()
    val marckerList by myViewModel.marckerList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        myViewModel.getAllMarkers()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(
            marckerList,
            key = { _, marker -> marker.id!! }
        ) { _, marker ->

            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        myViewModel.deleteMarker(marker.id!!)
                        true
                    } else false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {

                    // Usamos el progreso del swipe para escalar/animar el icono
                    val progress = dismissState.progress

                    val scale = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                        1.2f
                    } else {
                        1f
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(30.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                        )
                    }
                }
            ) {
                MarkerItem(marker) {
                    navigateToDetail(marker.id!!)
                }
            }
        }
    }
}*/


@Composable
fun List(navigateToDetail: (Int) -> Unit) {
    val myViewModel: MapsViewModel = viewModel()
    val marckerList by myViewModel.marckerList.observeAsState(emptyList())
    val snackbarHostState =
        remember { SnackbarHostState() } //Es el estado del Snackbar que mostrará el mensaje "Marcador eliminado" y tendrá un botón de deshacer.
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Marcador?>(null) } //Aquí almacenamos el marcador que está pendiente de eliminar. Si el usuario confirma la eliminación, se borra, y si se arrepiente, puede restaurarlo.

    LaunchedEffect(Unit) {
        myViewModel.getAllMarkers()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(paddingValues)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(
                marckerList,
                key = { _, marker -> marker.id!! }
            ) { _, marker ->

                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.StartToEnd) {
                            pendingDelete = marker
                            showDialog = true
                            false // Esperar confirmación
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.DarkGray)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                ) {
                    MarkerItem(marker) {
                        navigateToDetail(marker.id!!)
                    }
                }
            }
        }

        // AlertDialog de confirmación
        if (showDialog && pendingDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    pendingDelete = null
                },
                title = { Text("¿Eliminar marcador?") },
                text = { Text("¿Estás seguro de que deseas eliminar este marcador?") },
                confirmButton = {
                    TextButton(onClick = {
                        val deletedMarker = pendingDelete!!
                        myViewModel.deleteMarker(deletedMarker.id!!)
                        showDialog = false
                        pendingDelete = null

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Marcador eliminado",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                myViewModel.insertMarker(deletedMarker)
                            }
                        }
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        pendingDelete = null
                    }) {
                        Text("Cancelar")
                    }
                }
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = marcador.image_url,
                contentDescription = marcador.nombre,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            Text(
                text = "Estoy ubicado en: ${marcador.nombre}",
                color = Color.Black,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}