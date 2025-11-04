package org.dancorp.cyberclubadmin.ui.composables.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
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
fun PendingUserCard(
    user: User,
    currentUser: User?,
    onVerify: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = BorderStroke(1.dp, Color(0xFFFEF3C7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.email, style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color.Gray))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = "Verified", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Не подтвержден", color = Color.Green, fontSize = 10.sp)
                        }
                        // Text("Не подтвержден", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                Text(
                    text = "Зарегистрирован: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(user.createdAt)}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            if (currentUser?.verified == true) {
                Button(
                    onClick = onVerify,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = "Verify", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Подтвердить")
                }
            }
        }
    }
}
