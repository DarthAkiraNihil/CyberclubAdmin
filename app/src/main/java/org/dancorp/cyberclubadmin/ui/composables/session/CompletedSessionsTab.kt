package org.dancorp.cyberclubadmin.ui.composables.session

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun CompletedSessionsTab(
    sessions: List<Session>,
) {
    if (sessions.isEmpty()) {
        AlertCard(message = "Нет завершённых сессий. Чтобы они здесь появились, завершите хотя бы одну активную сессию")
    }

    Row {
        LazyColumn {
            items(sessions) { session ->
                CompletedSessionCard(session = session)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}