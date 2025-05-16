package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
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
import com.example.mapsapp.data.Marcador
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
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
        val stream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        _showLoading.value = false

        viewModelScope.launch(Dispatchers.IO) {
            val imageName = database.uploadImage(stream.toByteArray())
            val newMarck = Marcador(
                nombre = nombre,
                descripcion = descripcion,
                latitud = latitud,
                longitud = longitud,
                image_url = imageName
            )
            database.insertMarcador(newMarck)

            //Actualiza la lista para que la vista reaccione
            /*withContext(Dispatchers.Main) {*/
                /*delay(5000)*/

            val nuevos = database.getAllMarcador()
            withContext(Dispatchers.Main) {
                _marckerList.value = nuevos
                _showLoading.value = true
            }
                /*getAllMarkers()*/
            /*}*/
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
    fun updateMarker(id: Int, nombre: String, descripcion: String, image: Bitmap?){
        var imageName : String? = null
        var stream: ByteArrayOutputStream? = null
        if(image != null){
            stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 0, stream)
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
        CoroutineScope(Dispatchers.IO).launch{
            val marcador = database.getMarcador(id)
            withContext(Dispatchers.Main){
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



    fun removeMarker(latLng: LatLng) {
        _markerPositions.value -= latLng
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun setBitmap(bitmap: Bitmap) {
        _image.value = bitmapToBase64(bitmap)
    }


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
        _selectedMarker.value= null
    }

    fun clearSelectedMarkerAndResetCamera() {
        _selectedMarker.value = null

        viewModelScope.launch {
            // Solo si hay marcadores en la lista
            val markers = marckerList.value
            if (!markers.isNullOrEmpty()) {
                val builder = LatLngBounds.Builder()
                markers.forEach { marcador ->
                    builder.include(LatLng(marcador.latitud, marcador.longitud))
                }
                val bounds = builder.build()

                initialCameraPosition.animate(
                    update = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                )
            }
        }
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


