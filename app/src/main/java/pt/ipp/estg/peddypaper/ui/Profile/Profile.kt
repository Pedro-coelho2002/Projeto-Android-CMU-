package pt.ipp.estg.peddypaper.ui.Profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.ui.Firebase.FirestoreDataViewModel
import pt.ipp.estg.peddypaper.ui.Login.LoginViewModel
import pt.ipp.estg.peddypaper.ui.Room.QuestionViewModel
import pt.ipp.estg.peddypaper.ui.Room.User

@Composable
@Preview
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val firestoreDataViewModel: FirestoreDataViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val email = loginViewModel.fAuth.currentUser!!.email!!

    var currentUser: User? by remember { mutableStateOf(null) }
    firestoreDataViewModel.getUserByEmail(loginViewModel.fAuth.currentUser!!.email!!)
        .observeAsState().value?.let {
            currentUser = it
        }

    val currentUserTmp = currentUser

    val notification = rememberSaveable { mutableStateOf("") }
    if (notification.value.isNotEmpty()) {
        Toast.makeText(LocalContext.current, notification.value, Toast.LENGTH_LONG).show()
        notification.value = ""
    }

    val questionViewModel: QuestionViewModel = viewModel()
    val questions by questionViewModel.allQuestions.observeAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = currentUserTmp?.name.toString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = currentUserTmp?.password.toString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("Password") }
        )

        OutlinedTextField(
            value = currentUserTmp?.email.toString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = currentUserTmp?.numeroDeTelefone.toString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("Numero de telemóvel") }
        )

        OutlinedTextField(
            value = currentUserTmp?.points.toString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("Pontos") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.Gray)

        questions?.forEach { question ->
            var answerTMP by rememberSaveable { mutableStateOf("") }
            firestoreDataViewModel.getAnswer(email, question.id) { answer ->
                answerTMP = answer
            }

            ListItem(
                leadingContent = {
                    val icon = when {
                        answerTMP == question.correctAnswer -> Icons.Filled.CheckCircle
                        answerTMP == "" -> Icons.Filled.QuestionMark
                        else -> Icons.Filled.Cancel
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = "Status",
                        tint = when {
                            answerTMP == question.correctAnswer -> Color.Green
                            answerTMP == "" -> Color.Blue
                            else -> Color.Red
                        }
                    )
                },
                headlineText = {
                    Text(text = question.question)
                },
                supportingText = {
                    val answerText =
                        if (answerTMP.isNotEmpty()) "Respondeu: $answerTMP" else "Ainda não respondeu"
                    Text(text = answerText)
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Divider(color = Color.Gray)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("Menu Principal") }
            ) {
                Text("Voltar")
            }
        }
    }
}

@Composable
fun ProfileImage() {
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberImagePainter(
        if (imageUri.value.isEmpty())
            R.drawable.ic_user
        else
            imageUri.value
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri.value = it.toString() }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        }
        Text(text = "Mudar imagem de perfil")
    }
}
