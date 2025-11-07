package org.dancorp.cyberclubadmin.ui.composables.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.dancorp.cyberclubadmin.model.Subscription

@Composable
fun PayDebtDialog(
    onDismissRequest: () -> Unit,
    selectedSubForDebt: Subscription,
    onPayDebt: (Subscription, Double) -> Unit
) {

    var paidDebtAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Погасить долг",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Оплата долга по абонементу ${selectedSubForDebt?.subscriptionNumber ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Общий долг:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%.2f ₽".format(selectedSubForDebt?.debt ?: 0.0),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Сумма оплаты",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = paidDebtAmount,
                        onValueChange = { s -> paidDebtAmount = s },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        placeholder = {
                            Text("Введите сумму")
                        },
                        shape = MaterialTheme.shapes.small
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPayDebt(selectedSubForDebt, paidDebtAmount.toDoubleOrNull()!!)
                },
                enabled = paidDebtAmount.isNotEmpty() && paidDebtAmount.toDoubleOrNull()?.let { amount ->
                    amount > 0 && amount <= (selectedSubForDebt.debt)
                } ?: false
            ) {
                Text("Оплатить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = true
        )
    )

}