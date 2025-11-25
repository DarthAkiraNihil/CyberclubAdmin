package org.dancorp.cyberclubadmin.ui.composables.notification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractNotificationService

@Composable
fun NotificationsTabButton(
    user: User?,
    notificationsService: AbstractNotificationService,
    onClick: () -> Unit,
) {

    var unreadCount by remember { mutableIntStateOf(0) }

    fun updateUnreadCount() {
        CoroutineScope(Dispatchers.IO).async {
            val notifications = notificationsService.list()
            val unread = notifications.count { !it.read }
            unreadCount = unread
        }
    }

    LaunchedEffect(Unit) {
        if (user != null) {
            updateUnreadCount()
        }
    }

    BadgedBox(
        badge = {
            if (unreadCount > 0) {
                Badge {
                    Text(
                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}