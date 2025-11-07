package org.dancorp.cyberclubadmin.ui.composables.session

import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    show: Boolean,
    onDismiss: () -> Unit,
    availableTables: List<GameTable>,
    subscriptions: List<Subscription>,
    canCreateSession: (Subscription) -> ResultState,
    onCreateSession: (Subscription, GameTable, Int, Boolean) -> Unit
) {

    var selectedTable by remember { mutableStateOf("") }
    var selectedSubscription by remember { mutableStateOf("") }
    var bookedHours by remember { mutableStateOf("") }
    var payAsDebt by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (!show) {
        return
    }

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
                        onValueSelected = { selectedTable = it },
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
                        onValueSelected = { selectedSubscription = it },
                        placeholder = "Выберите абонемент"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hours input
                Column {
                    Text("Количество часов", style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = bookedHours,
                        onValueChange = { bookedHours = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pay as debt checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = payAsDebt,
                        onCheckedChange = { payAsDebt = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Записать в долг", style = MaterialTheme.typography.body2)
                }
            }
        },
        confirmButton = {
            Button(onClick = {

                if (selectedTable.isEmpty() || selectedSubscription.isEmpty()) {
                    Toast.makeText(context, "Выберите стол и абонемент", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val subscription = subscriptions.find { it.id == selectedSubscription }!!
                val table = availableTables.find { it.id == selectedTable }!!

                onCreateSession(
                    subscription,
                    table,
                    bookedHours.toIntOrNull()!!,
                    payAsDebt
                )
            }) {
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