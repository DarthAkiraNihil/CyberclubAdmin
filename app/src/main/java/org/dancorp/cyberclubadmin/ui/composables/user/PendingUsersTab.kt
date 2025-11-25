package org.dancorp.cyberclubadmin.ui.composables.user

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun PendingUsersTab(
    pendingUsers: List<User>,
    currentUser: User?,
    handleVerifyUser: (String) -> Unit,
    handleRejectUser: (String) -> Unit,
) {
    if (pendingUsers.isEmpty()) {
        AlertCard(message = "В данный момент нет пользователей, ожидающих подтверждения")
    }

    LazyColumn {
        items(pendingUsers) { user ->
            PendingUserCard(
                user = user,
                currentUser = currentUser,
                onVerify = { handleVerifyUser(user.id) },
                onReject = { handleRejectUser(user.id) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}