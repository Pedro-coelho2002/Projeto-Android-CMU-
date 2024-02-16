package pt.ipp.estg.peddypaper.ui.Login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val authState: MutableLiveData<AuthStatus>
    val fAuth: FirebaseAuth

    init {
        authState = MutableLiveData(AuthStatus.NOLOGGIN)
        fAuth = FirebaseAuth.getInstance()
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = fAuth.createUserWithEmailAndPassword(email, password).await()
                if (result != null && result.user != null) {
                    authState.postValue(AuthStatus.LOGGED)
                    Log.d("Register", "logged in")
                    return@launch
                }
                Log.d("Register", "anonymous")
                authState.postValue(AuthStatus.NOLOGGIN)
                return@launch
            } catch (e: Exception) {
            }
        }
    }

    fun login(email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            try {
                val result = fAuth.signInWithEmailAndPassword(email, password).await()
                if (result != null && result.user != null) {
                    authState.postValue(AuthStatus.LOGGED)
                    Log.d("Login", "logged in")
                    navController.navigate("Menu Principal")
                    return@launch
                }
                Log.d("Login", "anonymous")
                authState.postValue(AuthStatus.NOLOGGIN)
                return@launch
            } catch (e: Exception) {
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            fAuth.signOut()
            authState.postValue(AuthStatus.NOLOGGIN)
            Log.d("Login", "logout")
        }
    }

    enum class AuthStatus {
        LOGGED, NOLOGGIN
    }
}