package org.dancorp.cyberclubadmin.ui.composables.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun VerifiedUserCard(
    user: User,
    currentUser: User?,
    allUsers: List<User>,
    onRevokeVerification: () -> Unit
) {
    val isCurrentUser = user.id == currentUser?.id
    val verifier = user.verifiedBy?.let { verifierId ->
        allUsers.find { it.id == verifierId }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = user.email, style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.width(8.dp))
                if (isCurrentUser) {
                    Badge(containerColor = Color.Blue.copy(alpha = 0.1f)) {
                        Text("Вы", color = Color.Blue, fontSize = 10.sp)
                    }
                }
                Badge(
                    containerColor = Color.Green.copy(alpha = 0.1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Подтвержден", color = Color.Green, fontSize = 10.sp)
                    }
                }
            }

            Text(
                text = "Зарегистрирован: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(user.createdAt)}",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )

            if (verifier != null) {
                Text(
                    text = "Подтвердил: ${verifier.email}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onRevokeVerification,
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = "RevokeVerification",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Отозвать подтверждение")
            }
        }
    }
}
