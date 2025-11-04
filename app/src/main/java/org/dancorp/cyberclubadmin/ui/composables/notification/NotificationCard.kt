package org.dancorp.cyberclubadmin.ui.composables.notification

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.caption
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NotificationCard(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val isSessionExpired = notification.type == "session_expired"
    val isSubscriptionRenewal = notification.type == "subscription_renewal"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = if (notification.isRead) Color(0xFFF8F9FA) else Color(0xFFEFF6FF)),
        border = if (!notification.isRead) BorderStroke(1.dp, Color.Blue.copy(alpha = 0.3f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    if (isSessionExpired) {
                        Badge(containerColor = Color.Red) {
                            Text("Сессия завершена", color = Color.White, fontSize = 10.sp)
                        }
                    }
                    if (isSubscriptionRenewal) {
                        Badge(containerColor = Color.Blue) {
                            Text("Напоминание", color = Color.White, fontSize = 10.sp)
                        }
                    }
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Badge(
                            containerColor = Color.Transparent,
                            modifier = Modifier
                                .border(BorderStroke(2.dp, Color.Blue)),
                        ) {
                            Text("Новое", color = Color.Blue, fontSize = 10.sp)
                        }
                    }
                }

                // Actions
                Row {
                    if (!notification.isRead) {
                        IconButton(
                            onClick = onMarkAsRead,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.NotificationsOff, contentDescription = "Mark as read")
                        }
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message
            Text(
                text = notification.message,
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp
            Text(
                text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(notification.timestamp),
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
    }
}