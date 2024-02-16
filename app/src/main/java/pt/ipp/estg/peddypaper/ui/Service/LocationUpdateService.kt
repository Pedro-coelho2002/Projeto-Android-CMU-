package pt.ipp.estg.peddypaper.ui.Service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import pt.ipp.estg.peddypaper.MainActivity
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.ui.Room.Question
import pt.ipp.estg.peddypaper.ui.Room.QuestionDao
import pt.ipp.estg.peddypaper.ui.Room.QuestionDataBase
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationUpdateService : Service() {

    private lateinit var viewModel: LocationViewModel
    private lateinit var questionDao: QuestionDao
    private var questions: List<Question> = emptyList()

    private var currentLocation: LatLng? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "location_update_channel"
        const val QUESTION_PROXIMITY_THRESHOLD = 0.05
    }

    private val TAG = "LocationUpdateService"

    override fun onCreate() {
        super.onCreate()
        val db = QuestionDataBase.getDatabase(applicationContext)

        questionDao = db.getQuestionDao()
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(LocationViewModel::class.java)
        createNotificationChannel()
        Log.d(TAG, "LocationUpdateService onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "LocationUpdateService onStartCommand")


        if (checkLocationPermissions()) {
            startLocationUpdates()
        }

        return START_STICKY
    }

    private fun checkLocationPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locations: LocationResult) {
                for (location in locations.locations) {
                    Log.d(TAG, "Location changed: $location")
                    currentLocation = LatLng(location.latitude, location.longitude)
                    getQuestions { locationDetails ->
                        Log.d(TAG, "Location details updated: $locationDetails")
                    }
                    Log.d(TAG, "LocationUpdateService onLocationResult: $location")
                    checkQuestionProximity(currentLocation!!)
                }
            }
        }
        Log.d(TAG, "LocationUpdateService startLocationUpdates")
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            null
        )

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            it?.let { location ->
                Log.d(TAG, "Last known location: $location")
                // Atualize a localização atual e obtenha os detalhes do local
                currentLocation = LatLng(location.latitude, location.longitude)
                getQuestions { locationDetails ->
                    Log.d(TAG, "Location details updated: $locationDetails")
                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "Unable to get last known location", it)
        }
    }

    private fun createLocationRequest() =
        com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
        }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Location Update Channel"
            val descriptionText = "Channel for location updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private var ultimaquestao: String? = null
    private fun checkQuestionProximity(userLocation: LatLng) {
        Log.d(TAG, "Checking question proximity")
        Log.d(TAG, "Questions: $questions")
        for (question in questions) {
            val questionLocation = LatLng(
                question.local.latitude,
                question.local.longitude
            )

            val distance = calculateDistance(userLocation, questionLocation)
            Log.d(TAG, "Distance to question ${question.question}: $distance")
            if (distance <= QUESTION_PROXIMITY_THRESHOLD) {
                val currentQuestion = question.question
                if (currentQuestion != ultimaquestao) {
                    showProximityNotification(currentQuestion)
                    ultimaquestao = currentQuestion
                }
                break
            }
        }
    }


    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val PROXIMITY_NOTIFICATION_ID = 2
    private fun showProximityNotification(question: String) {
        Log.d(TAG, "Proximity notification: $question")

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Questão próxima")
            .setContentText(question)
            .setSmallIcon(R.drawable.paddypaper)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(PROXIMITY_NOTIFICATION_ID, notification)
    }

    private fun getQuestions(
        onQuestionsFetched: (List<Question>) -> Unit
    ) {
        questionDao.getAllQuestions().observeForever { allQuestions ->
            val questionsList = allQuestions.map {
                Question(
                    it.id,
                    it.question,
                    it.answers,
                    it.correctAnswer,
                    it.linkImage,
                    it.local,
                    it.likes,
                    it.dislikes
                )
            }
            questions = questionsList
            Log.e(TAG, "getQuestions: $questions")
            onQuestionsFetched(questions)
        }

    }

    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        return haversine(point1.latitude, point1.longitude, point2.latitude, point2.longitude)
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun stopLocationUpdates() {
        Log.d(TAG, "LocationUpdateService stopLocationUpdates")
        try {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            val locationCallback = object : LocationCallback() {}
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping location updates: ${e.message}", e)
        }
    }

}