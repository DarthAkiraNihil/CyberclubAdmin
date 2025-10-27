package org.dancorp.cyberclubadmin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UsersScreen() {
    var users by remember { mutableStateOf(emptyList<User>()) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    val context = LocalContext.current

    fun loadData() {
        users = Store.getUsers()
        currentUser = Store.getCurrentUser()
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun handleVerifyUser(userId: String) {
        val allUsers = Store.getUsers().toMutableList()
        val index = allUsers.indexOfFirst { it.id == userId }

        if (index != -1 && currentUser != null) {
            allUsers[index] = allUsers[index].copy(
                verified = true,
                verifiedBy = currentUser!!.id
            )
            Store.saveUsers(allUsers)
            Toast.makeText(context, "Пользователь подтвержден", Toast.LENGTH_SHORT).show()
            loadData()
        }
    }

    val pendingUsers = users.filter { !it.verified }
    val verifiedUsers = users.filter { it.verified }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Пользователи",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Управление администраторами",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pending users
        if (pendingUsers.isNotEmpty()) {
            Text(
                text = "Ожидают подтверждения",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(pendingUsers) { user ->
                    PendingUserCard(
                        user = user,
                        currentUser = currentUser,
                        onVerify = { handleVerifyUser(user.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Verified users
        Text(
            text = "Подтвержденные администраторы",
            style = MaterialTheme.typography.body2,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (verifiedUsers.isEmpty()) {
            AlertCard(message = "Нет пользователей")
        } else {
            LazyColumn {
                items(verifiedUsers) { user ->
                    VerifiedUserCard(
                        user = user,
                        currentUser = currentUser,
                        allUsers = users
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PendingUserCard(
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
                        Text("Не подтвержден", fontSize = 10.sp, color = Color.Gray)
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

@Composable
private fun VerifiedUserCard(
    user: User,
    currentUser: User?,
    allUsers: List<User>
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
        }
    }
}