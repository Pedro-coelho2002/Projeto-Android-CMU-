package pt.ipp.estg.peddypaper.ui.Service

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    val notificationContent = MutableLiveData("Updating user location")
}