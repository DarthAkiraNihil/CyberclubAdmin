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
fun VerifiedUsersTab(
    verifiedUsers: List<User>,
    currentUser: User?,
    users: List<User>,
    handleRevokeUserVerification: (String) -> Unit
) {
    if (verifiedUsers.isEmpty()) {
        AlertCard(message = "Нет пользователей")
    } else {
        LazyColumn {
            items(verifiedUsers) { user ->
                VerifiedUserCard(
                    user = user,
                    currentUser = currentUser,
                    allUsers = users,
                    onRevokeVerification = { handleRevokeUserVerification(user.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
