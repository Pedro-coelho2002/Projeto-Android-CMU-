package pt.ipp.estg.peddypaper.ui.MenuPrincipal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.ui.Firebase.FirestoreDataViewModel

@Composable
@Preview
fun MenuPrincipalPreview() {
    MenuPrincipal(navController = rememberNavController())
}

@Composable
fun MenuPrincipal(navController: NavController) {
    val firestoreDataViewModel: FirestoreDataViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.bem_vindo_ao_peddypapper),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.size(16.dp))

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            painter = painterResource(id = R.drawable.paddypaper),
            contentDescription = stringResource(id = R.string.app_name)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(R.string.instrucoes_1), fontSize = 18.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Text(
            text = stringResource(R.string.instrucoes_2), fontSize = 18.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Text(
            text = stringResource(R.string.instrucoes_3), fontSize = 18.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(
            onClick = {
                firestoreDataViewModel.getAnswersLive()
                navController.navigate("Jogar")
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, CircleShape)
                .height(70.dp),
            colors = ButtonDefaults.outlinedButtonColors(Color(219, 143, 20, 255)),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = stringResource(R.string.Jogar),
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

