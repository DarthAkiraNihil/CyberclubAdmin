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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType


@Composable
fun SubscriptionTypesTab(
    subscriptionTypes: List<SubscriptionType>,
    subscriptions: List<Subscription>,
    onCreateType: (String, Double, Double) -> Unit
) {

    var isCreateTypeDialogOpen by remember { mutableStateOf(false) }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { isCreateTypeDialogOpen = true },
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(subscriptionTypes) { type ->
                SubscriptionTypeCard(
                    type = type,
                    subscriptions = subscriptions
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    CreateSubscriptionTypeDialog(
        isCreateTypeDialogOpen,
        { isCreateTypeDialogOpen = false },
        { n, p, c ->
            onCreateType(n, p, c)
            isCreateTypeDialogOpen = false
        },
    )
}