package org.dancorp.cyberclubadmin.ui.composables.session

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun ActiveSessionsTab(
    sessions: List<Session>,
    subscriptions: List<Subscription>,
    gameTableService: AbstractGameTableService,
    handleExtendSession: (String) -> Unit,
    handleEndSession: (String) -> Unit,
) {
    if (sessions.isEmpty()) {
        AlertCard(message = "Нет активных или завершённых сессий. Создайте новую сессию.")
    }

    // Active Sessions
    Row {
        LazyColumn {
            items(sessions) { session ->
                val table = runBlocking { gameTableService.get(session.tableId)!! }
                val subscription = subscriptions.find { it.id == session.subscriptionId }
                val isExpired = session.remainingMinutes <= 0

                ActiveSessionCard(
                    session = session,
                    table = table,
                    subscription = subscription,
                    isExpired = isExpired,
                    onExtendSession = { handleExtendSession(session.id) },
                    onEndSession = { handleEndSession(session.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}