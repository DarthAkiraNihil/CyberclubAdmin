package org.dancorp.cyberclubadmin.ui.composables.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.getDaysUntilExpiry
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h6


@Composable
fun SubscriptionCard(
    sub: Subscription,
    onPayDebt: (Subscription, Double) -> Unit,
    onExtendSubscription: (Subscription) -> Unit,
    onRevokeSubscription: (Subscription) -> Unit
) {

    var isPayDebtDialogOpen by remember { mutableStateOf(false) }

    val isExpired = sub.isExpired()
    val isExpiringSoon = sub.isExpiringSoon()
    val type = sub.type
    val daysLeft = getDaysUntilExpiry(sub.expiryDate)

    if (isPayDebtDialogOpen) {
        PayDebtDialog(
            { isPayDebtDialogOpen = false },
            sub,
            onPayDebt = onPayDebt
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = when {
                    isExpired -> Color.Red.copy(alpha = 0.3f)
                    isExpiringSoon -> Color.Yellow.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = MaterialTheme.shapes.medium
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = sub.subscriptionNumber,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = sub.email,
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Badge(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = type.name,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Content
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Grid layout for details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Column 1
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Дней до окончания:",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                        Text(
                            text = if (daysLeft > 0) daysLeft.toString() else "Истек",
                            style = MaterialTheme.typography.body1,
                            color = when {
                                isExpired -> Color.Red
                                isExpiringSoon -> Color.Yellow
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    // Column 2
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Цена/месяц:",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                        Text(
                            text = "${type.pricePerMonth} ₽",
                            style = MaterialTheme.typography.body1
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Column 3
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Долг:",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                        Text(
                            text = "%.2f ₽".format(sub.debt),
                            style = MaterialTheme.typography.body1,
                            color = if (sub.debt > 0) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Column 4
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Не оплачено:",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                        Text(
                            text = sub.unpaidSessions.toString(),
                            style = MaterialTheme.typography.body1,
                            color = if (sub.unpaidSessions > 0) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Alert Section
                if (sub.debt > 0 || isExpiringSoon || isExpired) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Yellow.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = buildAnnotatedString {
                                if (sub.debt > 0) {
                                    append("Есть долг: %.2f ₽. ".format(sub.debt))
                                }
                                if (isExpired) {
                                    append("Абонемент истек! ")
                                }
                                if (isExpiringSoon && !isExpired) {
                                    append("Истекает через $daysLeft дн. ")
                                }
                            },
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (sub.debt > 0) {
                        Button(
                            onClick = { isPayDebtDialogOpen = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Pay",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Оплатить")
                        }
                    }

                    Button(
                        onClick = { onExtendSubscription(sub) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Extend",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Продлить")
                    }

                    Button(
                        onClick = { onRevokeSubscription(sub) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Revoke",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Отозвать")
                    }
                }
            }
        }
    }
}