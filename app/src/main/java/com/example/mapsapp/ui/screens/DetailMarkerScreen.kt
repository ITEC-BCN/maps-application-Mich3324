package com.example.mapsapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.R
import com.example.mapsapp.data.Marcador
import com.example.mapsapp.viewmodels.MapsViewModel
import java.io.File
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.RectangleShape


/*@Composable
fun DetailMarker(id: Int, navigateToEditar: (Int) -> Unit) {
    val myViewModel: MapsViewModel = viewModel<MapsViewModel>()
    val getMarker by myViewModel.selectedMarker.observeAsState()





    LaunchedEffect(getMarker) {
        myViewModel.getMarkerById(id)
    }




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

    val myViewModel: MapsViewModel = viewModel<MapsViewModel>()
    val getMarker by myViewModel.selectedMarker.observeAsState()
    val title by myViewModel.marckName.observeAsState("")
    val description by myViewModel.descripcion.observeAsState("")
    val imagen by myViewModel.image.observeAsState("")
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                val stream = context.contentResolver.openInputStream(imageUri!!)
                bitmap = BitmapFactory.decodeStream(stream)
            }
        }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                val stream = context.contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(stream)
            }
        }

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

            /*AsyncImage(
                model = marcador.image_url, // Aquí usa character.imageUrl si quieres cargar la imagen dinámica
                contentDescription = marcador.nombre,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Imagen de placeholder
                error = painterResource(id = R.drawable.ic_launcher_foreground) // Imagen de error si la carga falla
            )*/
            Spacer(modifier = Modifier.width(20.dp))

            Column( modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { myViewModel.editMarkerName(it) },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar descrpcion
                if (marcador.descripcion.isNotEmpty()) {
                    Text(
                        text = marcador.descripcion,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black

                    )
                }

                // Mostrar latitud de

                    Text(
                        text = "latitud: ${marcador.latitud}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )


                // Mostrar longitud
                Text(
                    text = "longitud: ${marcador.longitud}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )

            }

        }

    }
}*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailMarker(modifier : Modifier, id: Int, navigateBack: () -> Unit){
    var viewModel: MapsViewModel = viewModel()
    viewModel.getMarkerById(id)

    val title by viewModel.marckName.observeAsState("")
    val description by viewModel.descripcion.observeAsState("")
    val imagen by viewModel.image.observeAsState("")
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val selectedMarker by viewModel.selectedMarker.observeAsState(null)
    var showDialog by remember { mutableStateOf(false) }
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                val stream = context.contentResolver.openInputStream(imageUri!!)
                bitmap = BitmapFactory.decodeStream(stream)
            }
        }
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                val stream = context.contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(stream)
            }
        }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Editar marcador",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Icono de marcador",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Selecciona una opción") },
                text = { Text("¿Quieres tomar una foto o elegir una desde la galería?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        val uri = createImageUri(context)
                        imageUri = uri
                        takePictureLauncher.launch(uri!!)
                    }) {
                        Text("Tomar Foto")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        pickImageLauncher.launch("image/*")
                    }) {
                        Text("Elegir de Galería")
                    }
                }
            )
        }
        Button(onClick = { showDialog = true }) {
            Text("Selecciona una imagen")
        }
        Spacer(modifier = Modifier.height(24.dp))

        if(bitmap != null){
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
        else {
            Image(
                painter = rememberAsyncImagePainter(model = selectedMarker?.image_url),
                contentDescription = title,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RectangleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.editMarkerName(it) },
            label = { Text("${selectedMarker?.nombre}") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.editMarkerDescription(it)},
            label = { Text("${selectedMarker?.descripcion}") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            viewModel.updateMarker(id,title,description, bitmap)
            navigateBack()
        }) {
            Text("Guardar Marcador")
        }
    }
}

fun createImageUri(context: Context): Uri? {
    val file = File.createTempFile("temp_image_", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}