package com.example.workoutapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.workoutapp.viewmodels.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(locationPermissions)

    val fitnessStations by mapViewModel.fitnessStations.collectAsState()
    val isLoading by mapViewModel.isLoading.collectAsState()
    val errorMessage by mapViewModel.uiErrorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val cameraPositionState = rememberCameraPositionState()
    val previewCenter = cameraPositionState.position.target
    var previewRadius by remember { mutableStateOf(1000.0) }

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            mapViewModel.clearError()
        }
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness parks") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            when {
                permissionState.allPermissionsGranted -> {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        properties = MapProperties(isMyLocationEnabled = true),
                        uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                        cameraPositionState = cameraPositionState
                    ) {
                        Circle(
                            center = previewCenter,
                            radius = previewRadius,
                            strokeColor = Color.Green,
                            strokeWidth = 4f,
                            fillColor = Color.Green.copy(alpha = 0.3f)
                        )

                        fitnessStations.forEach { station ->
                            val lat = station.lat ?: station.center?.lat
                            val lon = station.lon ?: station.center?.lon

                            if (lat != null && lon != null) {
                                Marker(
                                    state = MarkerState(position = LatLng(lat, lon)),
                                    title = station.tags?.get("name") ?: "Fitness park",
                                    snippet = station.tags?.get("description") ?: "",
                                    //icon = bitmapDescriptorFromVector(context, R.drawable.ic_fitness_park, sizeInDp = 36f)
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .width(48.dp)
                            .height(280.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.8f) // slider mérete
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Slider(
                                    value = previewRadius.toFloat(),
                                    onValueChange = { previewRadius = it.toDouble() },
                                    valueRange = 1000f..30000f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        //.requiredHeight(540.dp)
                                        .requiredWidth(220.dp)
                                        .rotate(270f)
                                )
                            }

                            Text(
                                text = "${(previewRadius / 1000).toInt()} km",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val hasPermission = locationPermissions.all {
                                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                            }

                            if (hasPermission) {
                                try {
                                    val centerLat = cameraPositionState.position.target.latitude
                                    val centerLon = cameraPositionState.position.target.longitude

                                    mapViewModel.loadFitnessStations(
                                        centerLat,
                                        centerLon,
                                        previewRadius.toInt()
                                    )
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 24.dp, start = 24.dp)
                            .fillMaxWidth(0.6f)
                    ) {
                        Text("Fetch fitness parks")
                    }
                }

                permissionState.permissions.any {
                    !it.status.isGranted && !it.status.shouldShowRationale
                } -> {
                    PermissionSettingsPrompt()
                }

                else -> {
                    PermissionRationale(onRequestPermission = {
                        permissionState.launchMultiplePermissionRequest()
                    })
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                awaitFirstDown().consume()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    LaunchedEffect(fitnessStations) {
        if (fitnessStations.isNotEmpty()) {
            Log.d("MapScreen", "Fetched fitness parks:")
            fitnessStations.forEach {
                val lat = it.lat ?: it.center?.lat
                val lon = it.lon ?: it.center?.lon
                Log.d("MapScreen", "ID: ${it.id}, Lat: $lat, Lon: $lon, Tags: ${it.tags}")
            }
        }
    }
}



@Composable
private fun PermissionRationale(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location access is required to use the map.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Request permission")
        }
    }
}

@Composable
private fun PermissionSettingsPrompt() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Permission was permanently denied. Please enable it in Settings > Apps.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        // (Nem kötelező, de akár ide lehet egy "Ugrás a beállításokhoz" gomb is)
    }
}





