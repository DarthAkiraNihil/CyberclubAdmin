package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.dancorp.cyberclubadmin.ui.composables.user.PendingUsersTab
import org.dancorp.cyberclubadmin.ui.composables.user.VerifiedUsersTab
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.TabButton


private enum class UsersScreenTab {
    PENDING_USERS,
    VERIFIED_USERS
}
@Composable
fun UsersScreen(
    parentActivity: Activity,
    authService: AbstractAuthService,
    userService: AbstractUserService,
) {
    var users by remember { mutableStateOf(emptyList<User>()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var selectedTab by remember { mutableStateOf(UsersScreenTab.PENDING_USERS) }


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

    fun handleRevokeUserVerification(userId: String) {

        CoroutineScope(Dispatchers.IO).async {
            userService.revoke(userId)
            parentActivity.runOnUiThread {
                Toast.makeText(context, "Верификация пользователя была отозвана", Toast.LENGTH_SHORT).show()
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            TabButton(
                text = "Заявки",
                isSelected = selectedTab == UsersScreenTab.PENDING_USERS,
                onClick = { selectedTab = UsersScreenTab.PENDING_USERS },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Подтверждённые",
                isSelected = selectedTab == UsersScreenTab.VERIFIED_USERS,
                onClick = { selectedTab = UsersScreenTab.VERIFIED_USERS },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            UsersScreenTab.PENDING_USERS -> PendingUsersTab(
                pendingUsers = pendingUsers,
                currentUser = currentUser,
                handleVerifyUser = ::handleVerifyUser,
                handleRejectUser = ::handleRevokeUserVerification,
            )
            UsersScreenTab.VERIFIED_USERS -> VerifiedUsersTab(
                verifiedUsers = verifiedUsers,
                currentUser = currentUser,
                users = users,
                handleRevokeUserVerification = ::handleRevokeUserVerification
            )
        }
    }
}
