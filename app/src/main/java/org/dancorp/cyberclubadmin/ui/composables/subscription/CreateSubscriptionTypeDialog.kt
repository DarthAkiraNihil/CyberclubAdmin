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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CreateSubscriptionTypeDialog(
    isTypeDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    onCreateType: (String, Double, Double) -> Unit,
) {

    var formName by remember { mutableStateOf("") }
    var formPricePerMonth by remember { mutableStateOf("") }
    var formTariffCoefficient by remember { mutableStateOf("") }

    fun resetForm() {
        formName = ""
        formPricePerMonth = ""
        formTariffCoefficient = ""
    }

    if (isTypeDialogOpen) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
                resetForm()
            },
            title = {
                Text("Новый тип абонемента")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Создание тарифного плана",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Name Input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Название")
                        OutlinedTextField(
                            value = formName,
                            onValueChange = { newName -> formName = newName },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Премиум")
                            },
                            singleLine = true
                        )
                    }

                    // Price Input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Цена в месяц (₽)")
                        OutlinedTextField(
                            value = formPricePerMonth,
                            onValueChange = { newValue -> formPricePerMonth = newValue
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            placeholder = {
                                Text("0")
                            }
                        )
                    }

                    // Tariff Coefficient Input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Коэффициент тарификации (0-1)")
                        OutlinedTextField(
                            value = formTariffCoefficient,
                            onValueChange = { newValue -> formTariffCoefficient = newValue },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            placeholder = {
                                Text("1.0")
                            }
                        )
                        Text(
                            text = "Скидка на игровое время. 1.0 = полная цена, 0.75 = скидка 25%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onCreateType(formName, formPricePerMonth.toDoubleOrNull()!!, formTariffCoefficient.toDoubleOrNull()!!) } ,
                    enabled = formName.isNotBlank() && formPricePerMonth.isNotBlank() && formPricePerMonth.isNotBlank()
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        resetForm()
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}