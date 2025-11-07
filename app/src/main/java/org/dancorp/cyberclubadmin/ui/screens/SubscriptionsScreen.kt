package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionService
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionTypeService
import org.dancorp.cyberclubadmin.ui.composables.subscription.SubscriptionTypesTab
import org.dancorp.cyberclubadmin.ui.composables.subscription.SubscriptionsTab
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.TabButton

private enum class SubscriptionsScreenTab {
    SUBSCRIPTIONS,
    TYPES
}

@Composable
fun SubscriptionsScreen(
    parentActivity: Activity,
    subscriptionService: AbstractSubscriptionService,
    subscriptionTypeService: AbstractSubscriptionTypeService,
) {
    var subscriptions by remember { mutableStateOf(emptyList<Subscription>()) }
    var subscriptionTypes by remember { mutableStateOf(emptyList<SubscriptionType>()) }
    var selectedTab by remember { mutableStateOf(SubscriptionsScreenTab.SUBSCRIPTIONS) }

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {

            Log.i("app", "getting sub screen data")

            try {
                subscriptions = subscriptionService.list()
                subscriptionTypes = subscriptionTypeService.list()
            } catch (e: Throwable) {
                Log.e("app", e.toString())
            }


            Log.v("app", "sub list $subscriptions")

        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun handleCreateSubscription(email: String, type: SubscriptionType) {
        CoroutineScope(Dispatchers.IO).async {

            val result = subscriptionService.create(email, type)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()
        }
    }

    fun handlePayDebt(sub: Subscription, amount: Double) {
        CoroutineScope(Dispatchers.IO).async {

            val result = subscriptionService.payDebt(sub, amount)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()
        }
    }

    fun handleExtendSubscription(sub: Subscription) {
        CoroutineScope(Dispatchers.IO).async {
            val result = subscriptionService.extendSubscription(sub)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()
        }
    }

    fun handleRevokeSubscription(sub: Subscription) {
        CoroutineScope(Dispatchers.IO).async {
            val result = subscriptionService.revokeSubscription(sub)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()

        }
    }

    fun handleCreateType(name: String, pricePerMonth: Double, tariffCoefficient: Double) {
        CoroutineScope(Dispatchers.IO).async {
            val result = subscriptionTypeService.create(name, pricePerMonth, tariffCoefficient)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()
        }
    }

    // ... Other functions would be implemented similarly to previous screens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Абонементы",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Управление подписками клиентов",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            TabButton(
                text = "Абонементы",
                isSelected = selectedTab == SubscriptionsScreenTab.SUBSCRIPTIONS,
                onClick = { selectedTab = SubscriptionsScreenTab.SUBSCRIPTIONS },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Типы",
                isSelected = selectedTab == SubscriptionsScreenTab.TYPES,
                onClick = { selectedTab = SubscriptionsScreenTab.TYPES },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            SubscriptionsScreenTab.SUBSCRIPTIONS -> SubscriptionsTab(
                subscriptions = subscriptions,
                subscriptionTypes = subscriptionTypes,
                onCreateSubscription = ::handleCreateSubscription,
                onPayDebt = ::handlePayDebt,
                onExtendSubscription = ::handleExtendSubscription,
                onRevokeSubscription = ::handleRevokeSubscription,
            )
            SubscriptionsScreenTab.TYPES -> SubscriptionTypesTab(
                subscriptionTypes = subscriptionTypes,
                subscriptions = subscriptions,
                onCreateType = ::handleCreateType
            )
        }
    }

    // Dialogs would be implemented here similar to previous screens
}