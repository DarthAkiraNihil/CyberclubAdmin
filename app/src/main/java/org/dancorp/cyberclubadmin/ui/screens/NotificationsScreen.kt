package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.service.AbstractNotificationService
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.caption
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NotificationsScreen(
    parentActivity: Activity,
    notificationService: AbstractNotificationService
) {
    var notifications by remember { mutableStateOf(emptyList<Notification>()) }

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            notifications = notificationService.list()
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }


    fun handleMarkAsRead(notificationId: String) {
        CoroutineScope(Dispatchers.IO).async {
            val allNotifications = notificationService.list().toMutableList()
            val index = allNotifications.indexOfFirst { it.id == notificationId }

            if (index != -1) {
                allNotifications[index] = allNotifications[index].copy(isRead = true)
                notificationService.update(allNotifications[index].id, allNotifications[index])
                loadData()
            }
        }

    }

    fun handleDelete(notificationId: String) {
        CoroutineScope(Dispatchers.IO).async {

            notificationService.delete(notificationId)
            parentActivity.runOnUiThread {
                Toast.makeText(context, "Уведомление удалено", Toast.LENGTH_SHORT).show()
            }
            loadData()

        }
    }

    fun handleMarkAllAsRead() {
        val allNotifications = notifications.map { it.copy(isRead = true) }
        // Store.saveNotifications(allNotifications)
        Toast.makeText(context, "Все уведомления прочитаны", Toast.LENGTH_SHORT).show()
        loadData()
    }

    val unreadCount = notifications.count { !it.isRead }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Уведомления",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (unreadCount > 0) "$unreadCount непрочитанных" else "Нет новых уведомлений",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            if (unreadCount > 0) {
                Button(
                    onClick = { handleMarkAllAsRead() },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Icon(Icons.Default.NotificationsOff, contentDescription = "Mark all read", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Прочитать все", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notifications.isEmpty()) {
            AlertCard(message = "Нет уведомлений")
        } else {
            LazyColumn {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = { handleMarkAsRead(notification.id) },
                        onDelete = { handleDelete(notification.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
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