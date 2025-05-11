package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.MutableLiveData
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.data.Marcador
import com.example.mapsapp.data.MarcadorRepository
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MapsViewModel(private val supabaseClient: SupabaseClient) : ViewModel() {
    val repository = MarcadorRepository(supabaseClient)
    val database = MyAppSingleton.database


    // Usamos MutableStateFlow para que Compose pueda reaccionar a los cambios
    private val _markerPositions = MutableStateFlow<List<LatLng>>(emptyList())
    val markerPositions: StateFlow<List<LatLng>> = _markerPositions

    val initialCameraPosition = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(LatLng(41.3851, 2.1734), 12f)
    )

    // lista Marcadores

    private val _MarckerList = MutableLiveData<List<Marcador>>()
    val studentsList = _MarckerList

    //seleccionar Marcador

    private var _selectedMarker: Marcador? = null



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
        image?.compress(Bitmap.CompressFormat.PNG, 0, stream)
        CoroutineScope(Dispatchers.IO).launch {
            val imageName = database.uploadImage(stream.toByteArray())
            val newMarck = Marcador(
                nombre = nombre,
                descripcion = descripcion,
                latitud = latitud,
                longitud = longitud,
                image_url = imageName
            )
            repository.insertMarcador(newMarck)
            //repository.getAllMarcadores()
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
fun insertNewStudent(name: String, mark: String, image: Bitmap?) {
   val stream = ByteArrayOutputStream()
   image?.compress(Bitmap.CompressFormat.PNG, 0, stream)
   CoroutineScope(Dispatchers.IO).launch {
       val imageName = database.uploadImage(stream.toByteArray())

       database.insertStudent(name, mark.toDouble(), imageName)
   }
}
*/

    fun getAllStudents() {
        CoroutineScope(Dispatchers.IO).launch {
            val databaseStudents = repository.getAllMarcadores()
            withContext(Dispatchers.Main) {
                _MarckerList.value = databaseStudents
            }
        }
    }

    fun updateMarcker(id: String, name: String, descripcion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateMarcador(id, name, descripcion)
        }
    }

    fun deleteStudent(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteMarcador(id)
            getAllStudents()
        }
    }

    fun getStudent(id: String) {
        if (_selectedMarker == null) {
            CoroutineScope(Dispatchers.IO).launch {
                val marcador = repository.getMarcador(id)
                withContext(Dispatchers.Main) {
                    _selectedMarker = marcador
                    _marckername.value = marcador.nombre
                }
            }
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

}




    /*fun onDescripcionChange(newDescripcion: String) {
    _descripcion.value = newDescripcion
}

fun resetFormulario() {
    _marckername.value = ""
    _descripcion.value = ""
}


    /*fun guardarUbicacionCompleta(lat: Double, lng: Double) {
        val data = buildJsonObject {
            put("latitud", lat)
            put("longitud", lng)
            put("nombre", name.value)
            put("descripcion", descripcion.value)
        }*/

        viewModelScope.launch {
            try {
                MyAppSingleton.database.client
                    .postgrest["Marcador"]
                    .insert(data)
                cargarUbicacionesDesdeSupabase()
                resetFormulario()
            } catch (e: Exception) {
                Log.e("Supabase", "Error al guardar ubicación completa", e)
            }
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun cargarUbicacionesDesdeSupabase() {
        viewModelScope.launch {
            try {
                val response = MyAppSingleton.database.client
                    .postgrest["ubicaciones"]
                    .select()

                val body = response.data.toString()

                val ubicaciones: List<Marcador> = json.decodeFromString(body)
                val posiciones = ubicaciones.map { LatLng(it.latitud, it.longitud) }
                _markerPositions.value = posiciones

            } catch (e: Exception) {
                Log.e("Supabase", "Error al cargar ubicaciones", e)
            }
        }
    }

}*/

