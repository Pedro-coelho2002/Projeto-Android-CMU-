package pt.ipp.estg.peddypaper.ui.Login

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
@Preview
fun SignUpPreview() {
    SignUp(navController = rememberNavController())
}

@Composable
fun SignUp(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithTopBar(navController)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(navController: NavController) {
    Scaffold(
        topBar = {
            CustomTopAppBar(navController, "Signup", true)
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var name by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }
                var email by rememberSaveable { mutableStateOf("") }
                var numeroDeTelefone by rememberSaveable { mutableStateOf("") }
                var points by rememberSaveable { mutableStateOf(0) }

                val viewModel: LoginViewModel = viewModel()
                val authStatus = viewModel.authState.observeAsState()
                val context = LocalContext.current

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        label = { Text("Nome") }
                    )

                    OutlinedTextField(
                        value = numeroDeTelefone,
                        onValueChange = { numeroDeTelefone = it },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        label = { Text("Número de telemóvel") }
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        label = { Text("Email") }
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        label = { Text("Password") }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate("loginPage") }
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                viewModel.register(email, password)
                                Log.e("Registo", "User ADICIONADO")
                                registerUser(
                                    name,
                                    email,
                                    password,
                                    numeroDeTelefone,
                                    points
                                )
                                navController.navigate("loginPage")
                            }
                        ) {
                            Text("Criar Conta")
                        }
                    }
                }
            }
        })
}

fun registerUser(
    name: String,
    email: String,
    password: String,
    numeroDeTelefone: String,
    points: Int
) {
    val db = Firebase.firestore

    val questions = hashMapOf(
        "1" to "",
        "2" to "",
        "3" to "",
        "4" to "",
        "5" to "",
        "6" to "",
        "7" to "",
        "8" to "",
        "9" to "",
        "10" to "",
    )

    val user = hashMapOf(
        "name" to name,
        "email" to email,
        "password" to password,
        "numeroDeTelefone" to numeroDeTelefone,
        "points" to points,
        "answers" to questions,
    )

    db.collection("users")
        .document(email)
        .set(user)
        .addOnSuccessListener { documentReference ->
            Log.d("Registo", "DocumentSnapshot added with ID: ${documentReference}")
        }
        .addOnFailureListener { e ->
            Log.w("Registo", "Error adding document", e)
        }
}