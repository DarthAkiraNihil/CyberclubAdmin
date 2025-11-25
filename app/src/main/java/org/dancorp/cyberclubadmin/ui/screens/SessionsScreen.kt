package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.service.AbstractNotificationService
import org.dancorp.cyberclubadmin.service.AbstractSessionService
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionService
import org.dancorp.cyberclubadmin.ui.composables.session.ActiveSessionsTab
import org.dancorp.cyberclubadmin.ui.composables.session.CompletedSessionsTab
import org.dancorp.cyberclubadmin.ui.composables.session.CreateSessionDialog
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.TabButton
import java.util.Date
import kotlin.math.max

private enum class SessionsScreenTab {
    ACTIVE_SESSIONS,
    COMPLETED_SESSIONS
}

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
    var availableTables by remember { mutableStateOf(emptyList<GameTable>()) }

    var isCreateDialogOpen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(SessionsScreenTab.ACTIVE_SESSIONS) }

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            sessions = sessionService.list().sortedByDescending { it.active }
            subscriptions = subscriptionService.listActive()
            availableTables = gameTableService.listAvailableTables()
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun handleCreateSession(sub: Subscription, table: GameTable, bookedHours: Int, payAsDebt: Boolean) {

        CoroutineScope(Dispatchers.IO).async {
            val result = sessionService.create(
                sub,
                table,
                bookedHours,
                payAsDebt,
            )

            parentActivity.runOnUiThread {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }

            isCreateDialogOpen = false
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

            if (selectedTab == SessionsScreenTab.ACTIVE_SESSIONS) {

                Button(
                    onClick = { isCreateDialogOpen = true },
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Создать")
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            TabButton(
                text = "Активные",
                isSelected = selectedTab == SessionsScreenTab.ACTIVE_SESSIONS,
                onClick = { selectedTab = SessionsScreenTab.ACTIVE_SESSIONS },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Завершённые",
                isSelected = selectedTab == SessionsScreenTab.COMPLETED_SESSIONS,
                onClick = { selectedTab = SessionsScreenTab.COMPLETED_SESSIONS },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            SessionsScreenTab.ACTIVE_SESSIONS -> ActiveSessionsTab(
                sessions = sessions.filter { it.active },
                subscriptions = subscriptions,
                sessionService = sessionService,
                gameTableService = gameTableService,
                handleExtendSession = ::handleExtendSession,
                handleEndSession = ::handleEndSession,
            )

            SessionsScreenTab.COMPLETED_SESSIONS -> CompletedSessionsTab(
                sessions = sessions.filter { !it.active },
            )
        }

    }

    // Create Session Dialog
    CreateSessionDialog(
        show = isCreateDialogOpen,
        onDismiss = { isCreateDialogOpen = false },
        availableTables = availableTables,
        subscriptions = subscriptions,
        canCreateSession = Subscription::canCreateSession,
        onCreateSession = ::handleCreateSession
    )
}






