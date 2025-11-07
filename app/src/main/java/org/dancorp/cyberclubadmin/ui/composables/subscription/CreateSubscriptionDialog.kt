package org.dancorp.cyberclubadmin.ui.composables.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import org.dancorp.cyberclubadmin.model.SubscriptionType

@Composable
fun CreateSubscriptionDialog(
    isSubDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    subscriptionTypes: List<SubscriptionType>,
    onCreateSubscription: (String, SubscriptionType) -> Unit,
) {

    var formEmail by remember { mutableStateOf("") }
    var formType by remember { mutableStateOf(SubscriptionType()) }

    fun resetForm() {
        formEmail = ""
        formType = SubscriptionType()
    }

    if (!isSubDialogOpen) {
        return
    }

    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
            resetForm()
        },
        title = {
            Text("Новый абонемент")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Создание абонемента для клиента",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Email Input
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Email клиента")
                    OutlinedTextField(
                        value = formEmail,
                        onValueChange = { newEmail -> formEmail = newEmail },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("client@example.com")
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true
                    )
                }

                // Subscription Type Dropdown
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Тип абонемента")

                    var expanded by remember { mutableStateOf(false) }
                    val selectedType = subscriptionTypes.find { it.id == formType.id }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedType?.let { type ->
                                "${type.name} - ${type.pricePerMonth} ₽/мес (коэф. ${type.tariffCoefficient})"
                            } ?: "",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            placeholder = {
                                Text("Выберите тип")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            subscriptionTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${type.name} - ${type.pricePerMonth} ₽/мес (коэф. ${type.tariffCoefficient})")
                                    },
                                    onClick = {
                                        formType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreateSubscription(formEmail, formType) },
                enabled = formEmail.isNotBlank() && formType.id.isNotBlank()
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