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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.dancorp.cyberclubadmin.ui.composables.subscription.SubscriptionTypeCard
import org.dancorp.cyberclubadmin.ui.composables.subscription.SubscriptionsTab
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import org.dancorp.cyberclubadmin.ui.widgets.TabButton
import java.util.Calendar
import java.util.Date

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
    var isSubDialogOpen by remember { mutableStateOf(false) }
    var isTypeDialogOpen by remember { mutableStateOf(false) }

    var subFormData by remember {
        mutableStateOf(SubscriptionFormData())
    }

    var typeFormData by remember {
        mutableStateOf(SubscriptionTypeFormData())
    }

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {

            subscriptions = subscriptionService.list()
            subscriptionTypes = subscriptionTypeService.list()

        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }



    fun resetSubForm() {
        subFormData = SubscriptionFormData()
    }

    fun resetTypeForm() {
        typeFormData = SubscriptionTypeFormData()
    }

    fun handleCreateSubscription() {
        if (subFormData.email.isBlank()) {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.\\w+\$")
        if (!emailRegex.matches(subFormData.email)) {
            Toast.makeText(context, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).async {

            val allSubscriptions = subscriptionService.list()
            val existingActive = allSubscriptions.find {
                it.email == subFormData.email && it.isActive
            }

            if (existingActive != null) {
                Toast.makeText(context, "У этого email уже есть активный абонемент", Toast.LENGTH_SHORT).show()
                return@async
            }

            val purchaseDate = Date()
            val expiryDate = Calendar.getInstance().apply {
                time = purchaseDate
                add(Calendar.MONTH, 1)
            }.time

            val newSubscription = Subscription(
                id = System.currentTimeMillis().toString(),
                subscriptionNumber = "SUB-${System.currentTimeMillis().toString().takeLast(6)}",
                email = subFormData.email,
                type = SubscriptionType(),
                purchaseDate = purchaseDate,
                expiryDate = expiryDate,
                debt = 0.0,
                unpaidSessions = 0,
                isActive = true
            )

            subscriptionService.create(newSubscription)
            Toast.makeText(context, "Абонемент создан", Toast.LENGTH_SHORT).show()
            isSubDialogOpen = false
            resetSubForm()
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
                onClick = { SubscriptionsScreenTab.TYPES },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            SubscriptionsScreenTab.SUBSCRIPTIONS -> SubscriptionsTab(
                subscriptions = subscriptions,
                subscriptionTypes = subscriptionTypes,
                onOpenCreateDialog = { isSubDialogOpen = true }
            )
            SubscriptionsScreenTab.TYPES -> SubscriptionTypesTab(
                subscriptionTypes = subscriptionTypes,
                subscriptions = subscriptions,
                onOpenCreateDialog = { isTypeDialogOpen = true }
            )
        }
    }

    // Dialogs would be implemented here similar to previous screens
}

@Composable
fun SubscriptionTypesTab(
    subscriptionTypes: List<SubscriptionType>,
    subscriptions: List<Subscription>,
    onOpenCreateDialog: () -> Unit
) {
    TODO("Not yet implemented")
}

data class SubscriptionFormData(
    val email: String = "",
    val typeId: SubscriptionType = SubscriptionType()
)

data class SubscriptionTypeFormData(
    val name: String = "",
    val pricePerMonth: Double = 1000.0,
    val tariffCoefficient: Double = 1.0
)