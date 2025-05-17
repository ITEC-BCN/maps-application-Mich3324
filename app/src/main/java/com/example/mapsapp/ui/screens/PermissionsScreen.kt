package com.example.mapsapp.ui.screens



import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.utils.PermissionStatus
import com.example.mapsapp.viewmodels.PermissionViewModel


@Composable
//// Función composable que muestra la pantalla de permisos y recibe una función de navegación como parámetro Recibe un callback navigateToNext que se ejecuta cuando se conceden todos los permisos.
fun PermissionsScreen(navigateToNext: () -> Unit){
    val activity = LocalContext.current as? Activity //Se obtiene la actividad actual desde el context
    val viewModel = viewModel<PermissionViewModel>() // Se obtiene una instancia del ViewModel

    // Lista de permisos que se desean solicitar
    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    val permissionsStatus = viewModel.permissionsStatus.value //Estado actual de los permisos desde el ViewModel
    var alreadyRequested by remember { mutableStateOf(false) } // Bandera para saber si ya se solicitaron los permisos

    // Launcher que solicita múltiples permisos y devuelve un mapa con los resultados
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        // Se procesa el resultado de cada permiso solicitado
        permissions.forEach { permission ->
            val granted = result[permission] ?: false
            val status = when {
                granted -> PermissionStatus.Granted // Si fue concedido
                ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission) -> PermissionStatus.Denied
                else -> PermissionStatus.PermanentlyDenied
            }
            viewModel.updatePermissionStatus(permission, status) // Se actualiza el estado en el ViewModel
        }
    }

    // Se lanza el pedido de permisos al iniciar el composable, solo una vez
    LaunchedEffect(Unit) {
        if (!alreadyRequested) {
            alreadyRequested = true
            launcher.launch(permissions.toTypedArray())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Permissions status:", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        // Por cada permiso, se muestra su estado actual
        permissions.forEach { permission ->
            val status = permissionsStatus[permission]
            val label = when (status) {
                null -> "Solicitando..." // Estado inicial no se llega a visualizar
                PermissionStatus.Granted -> "Concedido"
                PermissionStatus.Denied -> "Denegado"
                PermissionStatus.PermanentlyDenied -> "Denegado Permanentemente"
            }
            val permissionName = permission.removePrefix("android.permission.")
            Text("$permissionName: $label")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Si algún permiso fue denegado (pero no permanentemente), se muestra un botón para volver a pedir
        if (permissions.any {
                permissionsStatus[it] == PermissionStatus.Denied
            }
        ) {
            Button(onClick = {
                launcher.launch(permissions.toTypedArray())
            }) {
                Text("Apply again")
            }
        }
        if (permissions.any {
                permissionsStatus[it] == PermissionStatus.PermanentlyDenied
            }
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // Se abre la configuración de la app para que el usuario cambie los permisos manualmente
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity!!.packageName, null)
                }
                activity!!.startActivity(intent)
            }) {
                Text("Ir a configuración")
            }
        }
    }
// Si todos los permisos fueron concedidos, se navega automáticamente a la siguiente pantalla
    LaunchedEffect(permissionsStatus) {
        val allGranted = permissions.all { permissionsStatus[it] == PermissionStatus.Granted }
        if (allGranted) {
            navigateToNext()
        }
    }
}

