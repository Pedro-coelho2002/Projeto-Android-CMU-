package pt.ipp.estg.peddypaper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import pt.ipp.estg.peddypaper.ui.Navigation.MyNavigatonDrawer
import pt.ipp.estg.peddypaper.ui.Sensores.LightSensorComponent
import pt.ipp.estg.peddypaper.ui.Service.LocationUpdateService
import pt.ipp.estg.peddypaper.ui.theme.PeddyPaperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sensorValue = LightSensorComponent()

            PeddyPaperTheme (darkTheme = sensorValue) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    startService(Intent(this, LocationUpdateService::class.java))
                    MyNavigatonDrawer()
                }
            }
        }
    }
}
