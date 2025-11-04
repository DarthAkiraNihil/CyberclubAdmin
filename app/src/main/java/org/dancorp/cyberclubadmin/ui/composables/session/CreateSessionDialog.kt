package org.dancorp.cyberclubadmin.ui.composables.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.ui.composables.shared.DropdownItem
import org.dancorp.cyberclubadmin.ui.composables.shared.DropdownMenuWrapper
import org.dancorp.cyberclubadmin.ui.theme.body2

@Composable
fun CreateSessionDialog(
    availableTables: List<GameTable>,
    subscriptions: List<Subscription>,
    selectedTable: String,
    selectedSubscription: String,
    bookedHours: Int,
    payAsDebt: Boolean,
    onTableSelect: (String) -> Unit,
    onSubscriptionSelect: (String) -> Unit,
    onBookedHoursChange: (Int) -> Unit,
    onPayAsDebtChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    canCreateSession: (Subscription) -> ResultState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая сессия") },
        text = {
            Column {
                Text(
                    text = "Создание игровой сессии",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Table selection
                Column {
                    Text("Стол", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownMenuWrapper(
                        items = availableTables.map {
                            DropdownItem(it.id, "Стол ${it.number} - ${it.hourlyRate} ₽/час")
                        },
                        selectedValue = selectedTable,
                        onValueSelected = onTableSelect,
                        placeholder = "Выберите стол"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subscription selection
                Column {
                    Text("Абонемент", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownMenuWrapper(
                        items = subscriptions.map { sub ->
                            val (allowed, reason) = canCreateSession(sub)
                            DropdownItem(
                                value = sub.id,
                                label = "${sub.subscriptionNumber} (${sub.email})${if (!allowed) " - $reason" else ""}",
                                enabled = allowed
                            )
                        },
                        selectedValue = selectedSubscription,
                        onValueSelected = onSubscriptionSelect,
                        placeholder = "Выберите абонемент"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hours input
                Column {
                    Text("Количество часов", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = bookedHours.toString(),
                        onValueChange = {
                            val value = it.toIntOrNull() ?: 1
                            if (value in 1..24) onBookedHoursChange(value)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pay as debt checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = payAsDebt,
                        onCheckedChange = onPayAsDebtChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Записать в долг", style = MaterialTheme.typography.body2)
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit) {
                Text("Создать сессию")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}