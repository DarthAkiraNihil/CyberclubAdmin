package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.runBlocking
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.service.AbstractNotificationService
import org.dancorp.cyberclubadmin.service.AbstractSessionService
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionService
import org.dancorp.cyberclubadmin.ui.composables.session.CompletedSessionCard
import org.dancorp.cyberclubadmin.ui.composables.session.CreateSessionDialog
import org.dancorp.cyberclubadmin.ui.composables.session.SessionCard
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import java.util.Date
import kotlin.math.max

@Composable
fun SessionsScreen(
    parentActivity: Activity,
    sessionService: AbstractSessionService,
    gameTableService: AbstractGameTableService,
    subscriptionService: AbstractSubscriptionService,
    notificationService: AbstractNotificationService
) {
    var sessions by remember { mutableStateOf(emptyList<Session>()) }
    var subscriptions by remember { mutableStateOf(emptyList<Subscription>()) }
    var tables by remember { mutableStateOf(emptyList<GameTable>()) }
    var isCreateOpen by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf("") }
    var selectedSubscription by remember { mutableStateOf("") }
    var bookedHours by remember { mutableIntStateOf(1) }
    var payAsDebt by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            sessions = sessionService.list()
            subscriptions = subscriptionService.listActive()
            tables = gameTableService.list()
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    suspend fun updateSessions() {
        val activeSessions = sessionService.list()
        val updatedSessions = activeSessions.map { session ->

            if (session.isActive) {
                session
                return
            }

            val elapsed = (System.currentTimeMillis() - session.startTime.time) / 60000
            val remaining = session.bookedMinutes - elapsed.toInt()

            if (remaining <= 0) {

                notificationService.create(
                    Notification(
                        id = System.currentTimeMillis().toString(),
                        type = "session_expired",
                        message = "Время сессии на столе ${session.tableId} истекло!",
                        timestamp = Date(),
                        isRead = false,
                        relatedId = session.id
                    )
                )
                parentActivity.runOnUiThread {
                    Toast.makeText(context, "Время сессии на столе ${session.tableId} истекло!", Toast.LENGTH_SHORT).show()
                }
            }

            session.copy(remainingMinutes = max(0, remaining))

        }

        updatedSessions.forEach { s -> {
            CoroutineScope(Dispatchers.IO).async {
                sessionService.update(s.id, s)
            }
        } }
        sessions = updatedSessions
    }

    fun handleCreateSession() {
        if (selectedTable.isEmpty() || selectedSubscription.isEmpty()) {
            Toast.makeText(context, "Выберите стол и абонемент", Toast.LENGTH_SHORT).show()
            return
        }

        val subscription = subscriptions.find { it.id == selectedSubscription }
        val table = tables.find { it.id == selectedTable }

        CoroutineScope(Dispatchers.IO).async {
            val result = sessionService.create(
                subscription,
                table,
                bookedHours,
                payAsDebt,
            )

            parentActivity.runOnUiThread {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }

            isCreateOpen = false
            selectedTable = ""
            selectedSubscription = ""
            bookedHours = 1
            payAsDebt = false
            loadData()
        }
    }

    fun handleExtendSession(sessionId: String) {
        CoroutineScope(Dispatchers.IO).async {
            sessionService.extend(sessionId)
            parentActivity.runOnUiThread {
                Toast.makeText(
                    context,
                    "Сессия была продлена на 1 час",
                    Toast.LENGTH_SHORT
                ).show()
            }
            loadData()
        }
    }

    fun handleEndSession(sessionId: String) {
        CoroutineScope(Dispatchers.IO).async {
            val actualPrice = sessionService.end(sessionId)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, "Сессия завершена. К оплате: ${String.format("%.2f", actualPrice)} ₽", Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()
        }
    }

    val activeSessions = sessions.filter { it.isActive }
    val completedSessions = sessions.filter { !it.isActive }.take(5)

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
                    text = "Игровые сессии",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Управление активными сессиями",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { isCreateOpen = true },
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Создать")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSessions.isEmpty()) {
            AlertCard(message = "Нет активных сессий. Создайте новую сессию.")
        }

        // Active Sessions
        LazyColumn {
            items(activeSessions) { session ->
                val table = runBlocking { gameTableService.get(session.tableId)!! }
                val subscription = subscriptions.find { it.id == session.subscriptionId }
                val isExpired = session.remainingMinutes <= 0

                SessionCard(
                    session = session,
                    table = table,
                    subscription = subscription,
                    isExpired = isExpired,
                    onExtend = { handleExtendSession(session.id) },
                    onEnd = { handleEndSession(session.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Completed Sessions
        if (completedSessions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Завершенные сессии",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(completedSessions) { session ->
                    CompletedSessionCard(session = session)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Create Session Dialog
    if (isCreateOpen) {
        CreateSessionDialog(
            availableTables = runBlocking { gameTableService.listAvailableTables() },
            subscriptions = subscriptions,
            selectedTable = selectedTable,
            selectedSubscription = selectedSubscription,
            bookedHours = bookedHours,
            payAsDebt = payAsDebt,
            onTableSelect = { selectedTable = it },
            onSubscriptionSelect = { selectedSubscription = it },
            onBookedHoursChange = { bookedHours = it },
            onPayAsDebtChange = { payAsDebt = it },
            onDismiss = { isCreateOpen = false },
            onSubmit = { handleCreateSession() },
            canCreateSession = Subscription::canCreateSession
        )
    }
}






