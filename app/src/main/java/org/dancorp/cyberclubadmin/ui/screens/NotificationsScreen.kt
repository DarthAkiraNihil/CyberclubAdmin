package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.service.AbstractNotificationService
import org.dancorp.cyberclubadmin.ui.composables.notification.NotificationCard
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

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
        notifications.map { it.copy(isRead = true) }
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
