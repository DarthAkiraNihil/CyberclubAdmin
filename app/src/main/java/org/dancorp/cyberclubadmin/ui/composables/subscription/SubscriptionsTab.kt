package org.dancorp.cyberclubadmin.ui.composables.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun SubscriptionsTab(
    subscriptions: List<Subscription>,
    subscriptionTypes: List<SubscriptionType>,
    onCreateSubscription: (String, SubscriptionType) -> Unit,
    onPayDebt: (Subscription, Double) -> Unit,
    onExtendSubscription: (Subscription) -> Unit,
    onRevokeSubscription: (Subscription) -> Unit,
) {

    var isCreateSubscriptionDialogOpen by remember { mutableStateOf(false) }

    val activeSubscriptions = subscriptions.filter { it.active }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { isCreateSubscriptionDialogOpen = true },
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Создать")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSubscriptions.isEmpty()) {
            AlertCard(message = "Нет активных абонементов. Создайте новый абонемент.")
        } else {
            Text(
                text = "Активные",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(activeSubscriptions) { sub ->
                    SubscriptionCard(
                        sub,
                        onPayDebt,
                        onExtendSubscription,
                        onRevokeSubscription
                    )
                }
            }
        }

        // Similar implementation for inactive subscriptions
    }

    CreateSubscriptionDialog(
        isCreateSubscriptionDialogOpen,
        { isCreateSubscriptionDialogOpen = false },
        subscriptionTypes,
        { s, t ->
            onCreateSubscription(s, t)
            isCreateSubscriptionDialogOpen = false
        }
    )
}