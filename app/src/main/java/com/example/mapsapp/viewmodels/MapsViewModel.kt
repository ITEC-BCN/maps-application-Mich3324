package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.data.model.Marcador
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MapsViewModel() : ViewModel() {
    val database = MyAppSingleton.database // y aqui llama a mi supabase con la funciones

    // Usamos MutableStateFlow para que Compose pueda reaccionar a los cambios
    private val _markerPositions = MutableStateFlow<List<LatLng>>(emptyList())
    val markerPositions: StateFlow<List<LatLng>> = _markerPositions

    val initialCameraPosition = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(LatLng(41.3851, 2.1734), 12f)
    )

    // lista Marcadores

    private val _marckerList = MutableLiveData<List<Marcador>>()
    val marckerList = _marckerList

    //seleccionar Marcador

    private var _selectedMarker = MutableLiveData<Marcador?>()
    var selectedMarker: LiveData<Marcador?> = _selectedMarker

    //is loading

    private val _showLoading = MutableLiveData<Boolean>()
    var showloading = _showLoading


    //crear nuevo marcador
    private val _marckername = MutableLiveData<String>()
    val marckName = _marckername
    private val _descripcion = MutableLiveData<String>()
    val descripcion = _descripcion
    val _latitud = MutableLiveData<Double>()
    val latitud = _latitud
    val _longitud = MutableLiveData<Double>()
    val longitud = _longitud
    val _image = MutableLiveData<String>()
    val image = _image


    @RequiresApi(Build.VERSION_CODES.O)
    fun insertNewMarker(
        nombre: String,
        descripcion: String,
        latitud: Double,
        longitud: Double,
        image: Bitmap?
    ) {
        // Convertimos la imagen Bitmap a bytes para subirla
        val stream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        _showLoading.value = false // indicamos que empieza la carga

        viewModelScope.launch(Dispatchers.IO) {
            // Subimos la imagen y obtenemos su nombre/url
            val imageName = database.uploadImage(stream.toByteArray())
            val newMarck = Marcador(
                nombre = nombre,
                descripcion = descripcion,
                latitud = latitud,
                longitud = longitud,
                image_url = imageName
            )
            database.insertMarcador(newMarck)
            // Actualizamos la lista para que la UI refleje el nuevo marcador
            val nuevos = database.getAllMarcador()
            withContext(Dispatchers.Main) {
                _marckerList.value = nuevos
                _showLoading.value = true
            }

        }
    }

    fun getAllMarkers() {
        viewModelScope.launch {
            showloading.value = true
            withContext(Dispatchers.IO) {
                val databaseMarker = database.getAllMarcador()
                withContext(Dispatchers.Main) {
                    _marckerList.value = databaseMarker
                    showloading.value = false
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMarker(id: Int, nombre: String, descripcion: String, image: Bitmap?) {
        var imageName: String? = null
        var stream: ByteArrayOutputStream? = null
        if (image != null) {
            stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 0, stream)
            // Extraemos el nombre de la imagen anterior para actualizarla (removiendo la URL base)
            imageName =
                _selectedMarker?.value?.image_url?.removePrefix("https://xlebkybtqrbnyowaavbq.supabase.co/storage/v1/object/public/images/")
        }
        CoroutineScope(Dispatchers.IO).launch {
            database.updateMarcador(id, nombre, descripcion, imageName, stream?.toByteArray())
        }
    }


    fun deleteMarker(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            database.deleteMarcador(id)
            getAllMarkers()
        }
    }


    fun getMarkerById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val marcador = database.getMarcador(id)
            withContext(Dispatchers.Main) {
                _selectedMarker.value = marcador
            }
        }

    }

    fun insertMarker(marcador: Marcador) {
        CoroutineScope(Dispatchers.IO).launch {
            database.insertMarcador(marcador)
            getAllMarkers()
        }
    }

    // Editores para los campos del marcador en la UI
    fun editMarkerName(name: String) {
        _marckername.value = name
    }

    fun editMarkerDescription(descripcion: String) {
        _descripcion.value = descripcion
    }

    fun setLatLng(lat: Double, lng: Double) {
        _latitud.value = lat
        _longitud.value = lng
    }

    // Convierte Bitmap a base64 para almacenarlo como string
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    // Asigna el string base64 de la imagen al LiveData
    fun setBitmap(bitmap: Bitmap) {
        _image.value = bitmapToBase64(bitmap)
    }

    // Valores por defecto para centrar la cámara en el mapa
    val defaultZoom = 10f
    val defaultLatLng = LatLng(41.3874, 2.1686)

    fun resetCamera() {
        _selectedMarker.value = null
        viewModelScope.launch {
            initialCameraPosition.animate(
                update = CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoom)
            )
        }
    }

    //para el card floating

    // Selecciona un marcador y centra la cámara en él con zoom 15
    fun selectMarker(marcador: Marcador) {
        _selectedMarker.value = marcador
        viewModelScope.launch {
            initialCameraPosition.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(marcador.latitud, marcador.longitud),
                    15f // Puedes ajustar el nivel de zoom (ej. entre 10 y 20)
                )
            )
        }
    }

    fun clearSelectedMarcador() {
        _selectedMarker.value = null
    }

    //para centrar la camara despues de cerrar el card
    fun centerMapOnAllMarkers() {
        val markers = marckerList.value ?: return
        if (markers.isEmpty()) return

        val boundsBuilder = LatLngBounds.builder()
        markers.forEach {
            boundsBuilder.include(LatLng(it.latitud, it.longitud))
        }
        val bounds = boundsBuilder.build()

        viewModelScope.launch {
            initialCameraPosition.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100) // padding 100
            )
        }
    }

}


