package pt.ipp.estg.peddypaper.ui.MapScreen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.ui.API.PlaceDetailsApi
import pt.ipp.estg.peddypaper.ui.API.RetrofitHelper
import pt.ipp.estg.peddypaper.ui.Firebase.FirestoreDataViewModel
import pt.ipp.estg.peddypaper.ui.Login.LoginViewModel
import pt.ipp.estg.peddypaper.ui.Models.Feature
import pt.ipp.estg.peddypaper.ui.Models.Location
import pt.ipp.estg.peddypaper.ui.Room.QuestionViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
@Preview
fun MapScreen1Preview() {
    MapScreen1(navController = rememberNavController())
}

@Composable
fun MapScreen1(navController: NavController) {
    val firestoreDataViewModel: FirestoreDataViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val questionViewModel: QuestionViewModel = viewModel()
    val questions by questionViewModel.allQuestions.observeAsState()
    var myLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val ctx = LocalContext.current
    var distanceTMP by remember { mutableStateOf(0.0) }
    val coroutineScope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Selecione uma questão para responder.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        val mapProperties by remember {
            mutableStateOf(
                MapProperties(maxZoomPreference = 17f, minZoomPreference = 5f)
            )
        }

        val mapUiSettings by remember {
            mutableStateOf(
                MapUiSettings(mapToolbarEnabled = false)
            )
        }

        val felgueiras = LatLng(41.36838, -8.19531)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(felgueiras, 12f)
            Log.e("Camera", "$position")
        }

        Location { location ->
            myLocation = LatLng(location.first, location.second)
        }

        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraPositionState,
            ) {
                val customIcon = createCustomIcon(R.drawable.current_location)
                Marker(
                    state = MarkerState(
                        position = myLocation
                    ),
                    icon = customIcon,
                )

                questions?.forEach { question ->
                    var respondida by rememberSaveable { mutableStateOf(false) }

                    firestoreDataViewModel.isAnswered(
                        loginViewModel.fAuth.currentUser!!.email!!,
                        question.id
                    ) { isAnswered ->
                        respondida = isAnswered
                    }

                    if (!respondida) {
                        // Ver esta parte do código para obter as informações do local
                        var locais by remember {
                            mutableStateOf<List<Feature>>(emptyList())
                        }
                        val InfoAPI =
                            RetrofitHelper.getInstance().create(PlaceDetailsApi::class.java)


                        InfoAPI.getDetails(
                            question.local.latitude,
                            question.local.longitude, "9b7ff05b3e0f4bedac87ebb2b5044e0e"
                        )
                            .enqueue(object : Callback<Location> {
                                override fun onResponse(
                                    call: Call<Location>,
                                    response: Response<Location>
                                ) {
                                    // Update the pointsList when the response is received
                                    val location = response.body()
                                    locais = location?.features ?: emptyList()
                                }

                                override fun onFailure(call: Call<Location>, t: Throwable) {
                                    // Handle failure if needed
                                }
                            })
                        Log.e("MapScreen1", "${locais}")


                        // Fim do código para obter as informações do local

                        val questionLoc = LatLng(
                            question.local.latitude,
                            question.local.longitude
                        )
                        locais.forEach { local ->
                            Log.e("MapScreen1", "${local.properties.name}")

                            MarkerInfoWindowContent(
                                state = MarkerState(
                                    position = LatLng(
                                        question.local.latitude,
                                        question.local.longitude
                                    )
                                ),
                                onInfoWindowLongClick = {
                                    coroutineScope.launch {
                                        distanceBetweenPoints(
                                            myLocation,
                                            questionLoc
                                        ) { distance -> distanceTMP = distance }

                                        Log.d(
                                            "MapScreen1",
                                            "Distância entre a minha localização e a questão ${question.id}: $distanceTMP"
                                        )

                                        if (distanceTMP < 0.05) { //0.05 Distância para responder a questão (em km)
                                            navController.navigate("question/${question.id}")
                                        } else {
                                            Toast.makeText(
                                                ctx,
                                                "Aproxime-se da questão para a responder.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            ) { marker ->
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        marker.title
                                            ?: "Questão ${question.id}: ${question.question} \n"
                                    )
                                    local.properties.formatted.split(",").forEach { part ->
                                        Text(part.trim())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun createCustomIcon(iconResource: Int): BitmapDescriptor {
    val density = LocalDensity.current.density
    val ctx = LocalContext.current
    val bitmap =
        Bitmap.createBitmap((48 * density).toInt(), (48 * density).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val icon = ContextCompat.getDrawable(ctx, iconResource)
    icon?.setBounds(0, 0, bitmap.width, bitmap.height)
    icon?.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun Location(onLocationReady: (Pair<Double, Double>) -> Unit) {
    val ctx = LocalContext.current

    val permission_given = remember {
        mutableStateOf(0)
    }
    if (ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        permission_given.value = 2
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                permission_given.value += 1
            }
        }
    LaunchedEffect(key1 = "Permission") {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    DisposableEffect(Unit) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            // onLocationReady(location.latitude to location.longitude)
            if (location != null) {
                onLocationReady(location.latitude to location.longitude)
            } else {
                Toast.makeText(ctx, "A última localização não está disponível.", Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnFailureListener {
            // Tratamento de erro, se necessário
            Toast.makeText(ctx, "Erro ao obter a localização !!", Toast.LENGTH_SHORT).show()
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000,
        ).setMinUpdateIntervalMillis(1000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locations: LocationResult) {
                for (loc in locations.locations) {
                    // onLocationReady(loc.latitude to loc.longitude)
                    if (loc != null) {
                        onLocationReady(loc.latitude to loc.longitude)
                    } else {
                        Toast.makeText(ctx, "A localização recebida é nula.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)

        onDispose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // raio da Terra em quilômetros

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c
}

fun distanceBetweenPoints(point1: LatLng, point2: LatLng, callback: (Double) -> Unit) {
    callback(haversine(point1.latitude, point1.longitude, point2.latitude, point2.longitude))
}