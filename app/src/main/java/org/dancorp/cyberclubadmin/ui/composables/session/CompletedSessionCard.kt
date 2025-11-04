package org.dancorp.cyberclubadmin.ui.composables.session

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.ui.theme.body2
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CompletedSessionCard(session: Session) {
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