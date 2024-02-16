package pt.ipp.estg.peddypaper.ui.QuestionScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import pt.ipp.estg.peddypaper.ui.Firebase.FirestoreDataViewModel
import pt.ipp.estg.peddypaper.ui.Login.LoginViewModel
import pt.ipp.estg.peddypaper.ui.Room.Question
import pt.ipp.estg.peddypaper.ui.Room.QuestionViewModel

@Preview
@Composable
fun QuestionScreenByQuestionNumberPreview() {
    QuestionScreenByQuestionNumber(navController = rememberNavController(), questionNumber = 3)
}

@Composable
fun QuestionScreenByQuestionNumber(navController: NavController, questionNumber: Int) {
    Log.d("QuestionScreen", "QuestionScreen: $questionNumber")

    val firestoreDataViewModel: FirestoreDataViewModel = viewModel()
    val questionViewModel: QuestionViewModel = viewModel()
    val questionTmp = questionViewModel.getQuestionById(questionNumber).observeAsState()

    Log.e("QuestionScreen", "questionTmp: ${questionTmp.value}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        questionTmp.value?.let { question ->
            // Pergunta
            Text(
                text = question.question,
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                style = MaterialTheme.typography.titleLarge
            )

            // Imagem
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                model = question.linkImage,
                contentDescription = null
            )

            // Botões de resposta
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AnswersButtons(question = question, navController = navController)

                Divider()

                Row {
                    var selected by remember { mutableStateOf(true) }
                    var showButtonLike by remember { mutableStateOf(true) }
                    var showButtonDislike by remember { mutableStateOf(true) }

                    ElevatedButton(
                        onClick = {
                            showButtonDislike = false
                            if (selected) {
                                firestoreDataViewModel.saveLikeAnser(question)
                                selected = false
                            }
                        },
                        enabled = showButtonLike || selected
                    ) {
                        Icon(imageVector = Icons.Outlined.ThumbUp, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "${question.likes}")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))

                    ElevatedButton(
                        onClick = {
                            showButtonLike = false
                            if (selected) {
                                firestoreDataViewModel.saveDislikeAnser(question)
                                selected = false
                            }
                        },
                        enabled = showButtonDislike || selected
                    ) {
                        Icon(imageVector = Icons.Outlined.ThumbDown, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "${question.dislikes}")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))

                    Button(
                        onClick = {
                            navController.navigate("Jogar")
                        }
                    ) {
                        Text("Mapa")
                    }

                }
            }
        }
    }
}

@Composable
@Preview
fun QuestionScreenByQuestionNumberPaisagemPreview() {
    QuestionScreenByQuestionNumberPaisagem(
        navController = rememberNavController(),
        questionNumber = 1
    )
}

@Composable
fun QuestionScreenByQuestionNumberPaisagem(navController: NavController, questionNumber: Int) {

    Log.d("QuestionScreen", "QuestionScreen: $questionNumber")

    val firestoreDataViewModel: FirestoreDataViewModel = viewModel()
    val questionViewModel: QuestionViewModel = viewModel()
    val questionTmp = questionViewModel.getQuestionById(questionNumber).observeAsState()

    Log.e("QuestionScreen", "questionTmp: ${questionTmp.value}")

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        questionTmp.value?.let { question ->

            Column(
                modifier = Modifier.weight(2f, true)
            ) {
                // Pergunta
                Text(
                    text = question.question,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    style = MaterialTheme.typography.titleLarge
                )

                // Imagem
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(250.dp),
                    model = question.linkImage,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = 32.dp))

            Column(modifier = Modifier.weight(2f, true)) {

                var selected by remember { mutableStateOf(true) }
                var showButtonLike by remember { mutableStateOf(true) }
                var showButtonDislike by remember { mutableStateOf(true) }

                Row(modifier = Modifier.padding(bottom = 16.dp)) {
                    ElevatedButton(
                        onClick = {
                            showButtonDislike = false
                            if (selected) {
                                firestoreDataViewModel.saveLikeAnser(question)
                                selected = false
                            }
                        },
                        enabled = showButtonLike || selected
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ThumbUp,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "${question.likes}")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))

                    ElevatedButton(
                        onClick = {
                            showButtonLike = false
                            if (selected) {
                                firestoreDataViewModel.saveDislikeAnser(question)
                                selected = false
                            }
                        },
                        enabled = showButtonDislike || selected
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ThumbDown,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "${question.dislikes}")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))

                    Button(
                        onClick = {
                            navController.navigate("Jogar")
                        }
                    ) {
                        Text("Mapa")
                    }
                }

                Divider()

                // Botões de resposta
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AnswersButtons(question = question, navController = navController)

                }
            }
        }
    }

}


@Composable
fun AnswersButtons(question: Question, navController: NavController) {
    val firestoreDataViewModel: FirestoreDataViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    var enableButton by remember { mutableStateOf(true) }
    var showDialogAnswerCorrect by remember { mutableStateOf(false) }
    var showDialogAnswerIncorrect by remember { mutableStateOf(false) }


    question.answers.forEach { answer ->
        var optionSelected by remember { mutableStateOf(false) }
        Button(
            onClick = {
                if (answer == question.correctAnswer && enableButton) { // Resposta correta
                    showDialogAnswerCorrect = true
                    optionSelected = true
                    enableButton = false
                    loginViewModel.fAuth
                    firestoreDataViewModel.saveAnswer(
                        loginViewModel.fAuth.currentUser!!.email!!,
                        question.id,
                        answer
                    )
                    firestoreDataViewModel.savePoints(loginViewModel.fAuth.currentUser!!.email!!)
                }
                if (answer != question.correctAnswer && enableButton) {
                    showDialogAnswerIncorrect = true
                    optionSelected = true
                    enableButton = false
                    firestoreDataViewModel.saveAnswer(
                        loginViewModel.fAuth.currentUser!!.email!!,
                        question.id,
                        answer
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (optionSelected) {
                    if (answer == question.correctAnswer) Color(0xFF4CAF50) else Color(0xFFEF5350)
                } else {
                    MaterialTheme.colorScheme.primary // Cor padrão quando a opção não foi selecionada
                }
            )
        ) {
            Text(text = answer, style = MaterialTheme.typography.bodyLarge)
        }
    }

    if (showDialogAnswerCorrect) {
        MyAlertDialog(
            onDismissRequest = { navController.navigate("Jogar") },
            onConfirmation = { showDialogAnswerCorrect = false },
            dialogTitle = "Pergunta correta !!",
            dialogText = "Ganhou 10 pontos\n" +
                    "\nQuer avaliar a questão?",
            icon = Icons.Filled.StarRate
        )
    }

    if (showDialogAnswerIncorrect) {
        MyAlertDialog(
            onDismissRequest = { navController.navigate("Jogar") },
            onConfirmation = { showDialogAnswerIncorrect = false },
            dialogTitle = "Pergunta Incorreta !!",
            dialogText = "Ganhou 0 pontos\n" +
                    "\nQuer avaliar a questão?",
            icon = Icons.Filled.StarRate
        )
    }

}

@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Star Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText, fontSize = 16.sp)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}
