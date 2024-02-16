package pt.ipp.estg.peddypaper.ui.Contacto

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun ContactarPreview() {
    ContactarScreen()
}

@Composable
fun ContactMethodRow(
    text: String,
    icon: ImageVector,
    onClick: (Context) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        // Ícone
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onClick(context) }
        )

        // Texto
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onClick(context) }
        )
    }
}

@Composable
fun ContactarScreen() {
    val email = "8210703@estg.ipp.pt"
    val telefone = "936582266"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Informações de Contato
        Text(
            text = "Informações de Contactos",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Nome
        Text(
            text = "Nome: Pedro Coelho",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // E-mail com botão
        ContactMethodRow("E-mail: $email", Icons.Default.Email) {
            sendEmail(it, email)
        }

        // Telefone com botão
        ContactMethodRow("Telefone: $telefone", Icons.Default.Phone) {
            makePhoneCall(it, telefone)
        }

        // Nome
        Text(
            text = "Nome: Pedro Araújo",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // E-mail com botão
        ContactMethodRow("E-mail: 8210539@estg.ipp.pt", Icons.Default.Email) {
            sendEmail(it, "8210539@estg.ipp.pt")
        }

        // Telefone com botão
        ContactMethodRow("Telefone: 937957896", Icons.Default.Phone) {
            makePhoneCall(it, "937957896")
        }
    }
}

fun sendEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
    }
    context.startActivity(intent)
}

fun makePhoneCall(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}