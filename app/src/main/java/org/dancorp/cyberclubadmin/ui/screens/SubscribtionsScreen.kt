package org.dancorp.cyberclubadmin.ui.screens

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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import org.dancorp.cyberclubadmin.ui.widgets.TabButton
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@Composable
fun SubscriptionsScreen() {
    var subscriptions by remember { mutableStateOf(emptyList<Subscription>()) }
    var subscriptionTypes by remember { mutableStateOf(emptyList<SubscriptionType>()) }
    var selectedTab by remember { mutableStateOf("subscriptions") }
    var isSubDialogOpen by remember { mutableStateOf(false) }
    var isTypeDialogOpen by remember { mutableStateOf(false) }
    var isPayDebtDialogOpen by remember { mutableStateOf(false) }
    var selectedSubForDebt by remember { mutableStateOf<Subscription?>(null) }
    var debtPaymentAmount by remember { mutableStateOf(0.0) }

    var subFormData by remember {
        mutableStateOf(SubscriptionFormData())
    }

    var typeFormData by remember {
        mutableStateOf(SubscriptionTypeFormData())
    }

    val context = LocalContext.current

    fun loadData() {
        subscriptions = Store.getSubscriptions()
        subscriptionTypes = Store.getSubscriptionTypes()
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
        if (subFormData.email.isBlank() || subFormData.typeId.isBlank()) {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.\\w+\$")
        if (!emailRegex.matches(subFormData.email)) {
            Toast.makeText(context, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return
        }

        val allSubscriptions = Store.getSubscriptions()
        val existingActive = allSubscriptions.find {
            it.email == subFormData.email && it.isActive
        }

        if (existingActive != null) {
            Toast.makeText(context, "У этого email уже есть активный абонемент", Toast.LENGTH_SHORT).show()
            return
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
            typeId = subFormData.typeId,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            debt = 0.0,
            unpaidSessions = 0,
            isActive = true
        )

        Store.saveSubscriptions(allSubscriptions + newSubscription)
        Toast.makeText(context, "Абонемент создан", Toast.LENGTH_SHORT).show()
        isSubDialogOpen = false
        resetSubForm()
        loadData()
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
                isSelected = selectedTab == "subscriptions",
                onClick = { selectedTab = "subscriptions" },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Типы",
                isSelected = selectedTab == "types",
                onClick = { selectedTab = "types" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            "subscriptions" -> SubscriptionsTab(
                subscriptions = subscriptions,
                subscriptionTypes = subscriptionTypes,
                onOpenCreateDialog = { isSubDialogOpen = true }
            )
            "types" -> SubscriptionTypesTab(
                subscriptionTypes = subscriptionTypes,
                subscriptions = subscriptions,
                onOpenCreateDialog = { isTypeDialogOpen = true }
            )
        }
    }

    // Dialogs would be implemented here similar to previous screens
}

@Composable
private fun SubscriptionsTab(
    subscriptions: List<Subscription>,
    subscriptionTypes: List<SubscriptionType>,
    onOpenCreateDialog: () -> Unit
) {
    val activeSubscriptions = subscriptions.filter { it.isActive }
    val inactiveSubscriptions = subscriptions.filter { !it.isActive }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = onOpenCreateDialog,
                modifier = Modifier.height(36.dp)
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
                items(activeSubscriptions) { subscription ->
                    // Subscription card implementation would go here
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Similar implementation for inactive subscriptions
    }
}

@Composable
private fun SubscriptionTypesTab(
    subscriptionTypes: List<SubscriptionType>,
    subscriptions: List<Subscription>,
    onOpenCreateDialog: () -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = onOpenCreateDialog,
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
}

@Composable
private fun SubscriptionTypeCard(
    type: SubscriptionType,
    subscriptions: List<Subscription>
) {
    val activeCount = subscriptions.count { it.typeId == type.id && it.isActive }
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

data class SubscriptionFormData(
    val email: String = "",
    val typeId: String = ""
)

data class SubscriptionTypeFormData(
    val name: String = "",
    val pricePerMonth: Double = 1000.0,
    val tariffCoefficient: Double = 1.0
)