package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

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

@Composable
private fun SessionCard(
    session: Session,
    table: GameTable?,
    subscription: Subscription?,
    isExpired: Boolean,
    onExtend: () -> Unit,
    onEnd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isExpired) BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Стол ${session.tableId}",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )
                        if (isExpired) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = Color.Red) {
                                Text("Время истекло", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                    Text(
                        text = subscription?.email ?: "",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
                Badge(
                    containerColor = if (session.isPaidForDebt) Color.LightGray else Color.Blue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (session.isPaidForDebt) "В долг" else "Оплата",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Session details
            GridLayout(
                items = listOf(
                    Pair("Забронировано:", "${session.bookedMinutes / 60} ч ${session.bookedMinutes % 60} мин"),
                    Pair("Осталось:", "${session.remainingMinutes / 60} ч ${session.remainingMinutes % 60} мин"),
                    Pair("Начало:", SimpleDateFormat("HH:mm", Locale.getDefault()).format(session.startTime)),
                    Pair("К оплате:", "${String.format("%.2f", session.finalPrice)} ₽")
                ),
                textColor = if (isExpired) Color.Red else Color.Unspecified
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onExtend,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = "Extend", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Продлить", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEnd,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "End", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Завершить", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CompletedSessionCard(session: Session) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Стол ${session.tableId}")
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(session.startTime),
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${String.format("%.2f", session.finalPrice)} ₽")
                Badge(
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Gray)),
                ) {
                    Text("Завершена", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun GridLayout(
    items: List<Pair<String, String>>,
    textColor: Color = Color.Unspecified
) {
    Column {
        items.chunked(2).forEach { chunk ->
            Row(modifier = Modifier.fillMaxWidth()) {
                chunk.forEach { (label, value) ->
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.body1,
                            color = textColor
                        )
                    }
                }
            }
            if (chunk.size < 2) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CreateSessionDialog(
    availableTables: List<GameTable>,
    subscriptions: List<Subscription>,
    selectedTable: String,
    selectedSubscription: String,
    bookedHours: Int,
    payAsDebt: Boolean,
    onTableSelect: (String) -> Unit,
    onSubscriptionSelect: (String) -> Unit,
    onBookedHoursChange: (Int) -> Unit,
    onPayAsDebtChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    canCreateSession: (Subscription) -> ResultState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая сессия") },
        text = {
            Column {
                Text(
                    text = "Создание игровой сессии",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Table selection
                Column {
                    Text("Стол", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownMenuWrapper(
                        items = availableTables.map {
                            DropdownItem(it.id, "Стол ${it.number} - ${it.hourlyRate} ₽/час")
                        },
                        selectedValue = selectedTable,
                        onValueSelected = onTableSelect,
                        placeholder = "Выберите стол"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subscription selection
                Column {
                    Text("Абонемент", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownMenuWrapper(
                        items = subscriptions.map { sub ->
                            val (allowed, reason) = canCreateSession(sub)
                            DropdownItem(
                                value = sub.id,
                                label = "${sub.subscriptionNumber} (${sub.email})${if (!allowed) " - $reason" else ""}",
                                enabled = allowed
                            )
                        },
                        selectedValue = selectedSubscription,
                        onValueSelected = onSubscriptionSelect,
                        placeholder = "Выберите абонемент"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hours input
                Column {
                    Text("Количество часов", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = bookedHours.toString(),
                        onValueChange = {
                            val value = it.toIntOrNull() ?: 1
                            if (value in 1..24) onBookedHoursChange(value)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pay as debt checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = payAsDebt,
                        onCheckedChange = onPayAsDebtChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Записать в долг", style = MaterialTheme.typography.body2)
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit) {
                Text("Создать сессию")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun DropdownMenuWrapper(
    items: List<DropdownItem>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = items.find { it.value == selectedValue }?.label ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text(placeholder) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }
        )

        // Invisible clickable surface
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable { expanded = true }
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    onValueSelected(item.value)
                    expanded = false
                },
                enabled = item.enabled,
                text = {
                    Text(
                        text = item.label,
                        color = if (item.enabled) Color.Unspecified else Color.Gray
                    )
                }
            )
        }
    }
}

data class DropdownItem(
    val value: String,
    val label: String,
    val enabled: Boolean = true
)