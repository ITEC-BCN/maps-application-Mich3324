package com.example.mapsapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.viewmodels.MapsViewModel
import com.example.mapsapp.viewmodels.MapsViewModelFactory
import kotlinx.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType")
@Composable
fun CreateMarker(
    navigateToDetail: (String) -> Unit,
    latd: Double,
    logd: Double
) {
    val context = LocalContext.current
    val factory = remember {
        MapsViewModelFactory(MyAppSingleton.database.client)
    }
    val myViewModel: MapsViewModel = viewModel(factory = factory)
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val marckName: String by myViewModel.marckName.observeAsState("")
    val marckDescripcion: String by myViewModel.descripcion.observeAsState("")
    val latitud: Double by myViewModel.latitud.observeAsState(latd)
    val longitud: Double by myViewModel.longitud.observeAsState(logd)
    val image: String by myViewModel.image.observeAsState("")

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmapIt ->
        if (bitmapIt != null) {
            bitmap.value = bitmapIt
            myViewModel.setBitmap(bitmap.value!!)
        }
    }

    // Launcher para la galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val bitmapGal = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmap.value = bitmapGal
                myViewModel.setBitmap(bitmapGal)
            } catch (e: IOException) {
                Toast.makeText(context, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón cámara
        OutlinedButton(onClick = { cameraLauncher.launch() }) {
            Icon(Icons.Default.Create, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Usar cámara")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón galería
        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Elegir desde galería")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen seleccionada
        bitmap.value?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text("Create newMarker", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = marckName,
            onValueChange = { myViewModel.editMarkerName(it) },
            label = { Text("Nombre del marcador") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = marckDescripcion,
            onValueChange = { myViewModel.editMarkerDescription(it) },
            label = { Text("Descripción del marcador") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            myViewModel.insertNewMarker(
                marckName,
                marckDescripcion,
                latitud,
                longitud,
                bitmap.value
            )
        }) {
            Text("Insertar marcador")
        }
    }

}

/*fun CreateMarker(
    navigateToDetail: (String) -> Unit,
    latitud: Double,
    longitud: Double
) {
    val factory = remember {
        MapsViewModelFactory(MyAppSingleton.database.client)
    }
    val myViewModel: MapsViewModel = viewModel(factory = factory)

    val marckName: String by myViewModel.marckName.observeAsState("")
    val marckDescripcion: String by myViewModel.descripcion.observeAsState("")
    val latitud:Double by myViewModel.latitud.observeAsState(0.0)
    val longitud:Double by myViewModel.longitud.observeAsState(0.0)
    val image:String by myViewModel.image.observeAsState("")


    LaunchedEffect(latitud, longitud) {
        myViewModel.setLatLng(latitud, longitud)
    }


    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text("Create newMarker", fontSize = 28.sp)
            TextField(value = marckName, onValueChange = { myViewModel.editMarkerName(it) })
            TextField(value = marckDescripcion, onValueChange = { myViewModel.editMarkerDescription(it) })
            Button(onClick = { myViewModel.insertNewMarker(marckName,marckDescripcion,latitud,longitud, image) }) {
                Text("Insert")
            }
        }
    }
}*/

/*Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text("Create newMarker", fontSize = 28.sp)
            TextField(value = studentName, onValueChange = { myViewModel.editStudentName(it) })
            TextField(value = studentMark, onValueChange = { myViewModel.editStudentMark(it) })
            Button(onClick = { myViewModel.insertNewStudent(studentName, studentMark) }) {
                Text("Insert")
            }
        }
    }
}*/