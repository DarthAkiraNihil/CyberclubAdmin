package org.dancorp.cyberclubadmin.ui.composables.session

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.ui.composables.shared.GridLayout
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h6
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.minutes


@SuppressLint("DefaultLocale")
@Composable
fun ActiveSessionCard(
    session: Session,
    table: GameTable?,
    subscription: Subscription?,
    isExpired: Boolean,
    onExtendSession: () -> Unit,
    onEndSession: () -> Unit,
    onTickTack: (session: Session) -> Unit
) {

    var remainingTime by remember { mutableIntStateOf(session.remainingMinutes) }

    LaunchedEffect(Unit) {

        while (remainingTime > 0) {
            delay(1.minutes)
            remainingTime-- // Decrement the remaining time
            onTickTack(session.copy(remainingMinutes = remainingTime))
            Log.i("app", "tick tack $remainingTime")
        }

    }

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
                            text = "Стол №${table?.number} (${session.tableId})",
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
                    containerColor = if (session.paidForDebt) Color.LightGray else Color.Blue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (session.paidForDebt) "В долг" else "Оплата",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Session details
            GridLayout(
                items = listOf(
                    Pair("Забронировано:", "${session.bookedMinutes / 60} ч ${session.bookedMinutes % 60} мин"),
//                    Pair("Осталось:", "${session.remainingMinutes / 60} ч ${session.remainingMinutes % 60} мин"),
                    Pair("Осталось:", "$remainingTime мин"),
                    Pair("Начало:", SimpleDateFormat("HH:mm", Locale.getDefault()).format(session.startTime)),
                    Pair("К оплате:", "${String.format("%.2f", session.finalPrice)} ₽")
                ),
                textColor = if (isExpired) Color.Red else Color.Unspecified
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onExtendSession,
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
                    onClick = onEndSession,
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