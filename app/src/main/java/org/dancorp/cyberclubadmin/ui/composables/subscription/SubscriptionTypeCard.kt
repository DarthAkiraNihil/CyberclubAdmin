package org.dancorp.cyberclubadmin.ui.composables.subscription

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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
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
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h6
import kotlin.math.roundToInt

@Composable
fun SubscriptionTypeCard(
    type: SubscriptionType,
    subscriptions: List<Subscription>
) {
    val activeCount = 0 // subscriptions.count { it.type == type.id && it.isActive }
    val discount = ((1 - type.tariffCoefficient) * 100).roundToInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = type.name, style = MaterialTheme.typography.h6)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = "Price", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${type.pricePerMonth} ₽/мес", style = MaterialTheme.typography.body2)
                    }

                    if (discount > 0) {
                        Badge(containerColor = Color.Green.copy(alpha = 0.1f)) {
                            Text("Скидка $discount%", color = Color.Green, fontSize = 10.sp)
                        }
                    }
                }

                if (activeCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Активных абонементов: $activeCount",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = { /* Handle delete */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}