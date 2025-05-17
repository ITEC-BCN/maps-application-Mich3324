package com.example.mapsapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mapsapp.viewmodels.MapsViewModel
import java.io.File
import androidx.compose.ui.graphics.RectangleShape

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailMarker(modifier : Modifier, id: Int, navigateBack: () -> Unit){
    var viewModel: MapsViewModel = viewModel()
    // Petición al ViewModel para obtener los datos del marcador por ID
    viewModel.getMarkerById(id)

    // Recolección de los estados observables del ViewModel
    val title by viewModel.marckName.observeAsState("")
    val description by viewModel.descripcion.observeAsState("")
    val imagen by viewModel.image.observeAsState("")
    val context = LocalContext.current
    // Estados locales de imagen y bitmap
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val selectedMarker by viewModel.selectedMarker.observeAsState(null)
    var showDialog by remember { mutableStateOf(false) }
    // Lanza cámara y obtiene imagen
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                val stream = context.contentResolver.openInputStream(imageUri!!)
                bitmap = BitmapFactory.decodeStream(stream)
            }
        }

    // Lanza selector de galería y obtiene imagen
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                val stream = context.contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(stream)
            }
        }

    //encabezado
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

//Contenido principal:
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Diálogo de selección de imagen:
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
        //Imagen mostrada:
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
        //Campos de texto
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

        //Botón guardar:
        Button(onClick = {
            viewModel.updateMarker(id,title,description, bitmap)
            navigateBack()
        }) {
            Text("Guardar Marcador")
        }
    }
}
//Función auxiliar para crear URI temporal
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