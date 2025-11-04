package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractAuthService
import org.dancorp.cyberclubadmin.service.AbstractUserService
import org.dancorp.cyberclubadmin.ui.composables.user.PendingUserCard
import org.dancorp.cyberclubadmin.ui.composables.user.VerifiedUserCard
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun UsersScreen(
    parentActivity: Activity,
    authService: AbstractAuthService,
    userService: AbstractUserService,
) {
    var users by remember { mutableStateOf(emptyList<User>()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            users = userService.list()
            currentUser = authService.currentUser
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun handleVerifyUser(userId: String) {

        CoroutineScope(Dispatchers.IO).async {
            userService.verify(userId, currentUser!!)
            parentActivity.runOnUiThread {
                Toast.makeText(context, "Пользователь подтвержден", Toast.LENGTH_SHORT).show()
            }
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
