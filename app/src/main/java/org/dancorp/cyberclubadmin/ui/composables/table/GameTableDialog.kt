package org.dancorp.cyberclubadmin.ui.composables.table

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.ui.theme.body2

data class GameTableFormData(
    val number: Int = 1,
    val cpu: String = "",
    val ram: Int = 16,
    val diskTotal: Int = 500,
    val gpu: String = "",
    val hourlyRate: Int = 100,
    val installedGames: MutableList<String> = mutableListOf()
)

@Composable
fun GameTableDialog(
    formData: GameTableFormData,
    games: List<Game>,
    isEditing: Boolean,
    onFormDataChange: (GameTableFormData) -> Unit,
    onGameToggle: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    calculateDiskUsed: (List<String>) -> Int
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Редактировать стол" else "Добавить стол") },
        text = {
            Column {
                Text(
                    text = "Настройка параметров игрового компьютера",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = formData.number.toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 1
                        onFormDataChange(formData.copy(number = value))
                    },
                    label = { Text("Номер стола") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isEditing
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formData.cpu,
                    onValueChange = { onFormDataChange(formData.copy(cpu = it)) },
                    label = { Text("Процессор") },
                    placeholder = { Text("Intel Core i7-12700K") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formData.gpu,
                    onValueChange = { onFormDataChange(formData.copy(gpu = it)) },
                    label = { Text("Видеокарта") },
                    placeholder = { Text("NVIDIA RTX 3070") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = formData.ram.toString(),
                        onValueChange = {
                            val value = it.toIntOrNull() ?: 16
                            onFormDataChange(formData.copy(ram = value))
                        },
                        label = { Text("ОЗУ (ГБ)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = formData.diskTotal.toString(),
                        onValueChange = {
                            val value = it.toIntOrNull() ?: 500
                            onFormDataChange(formData.copy(diskTotal = value))
                        },
                        label = { Text("Диск (ГБ)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formData.hourlyRate.toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 100
                        onFormDataChange(formData.copy(hourlyRate = value))
                    },
                    label = { Text("Цена (₽/час)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Games selection
                Column {
                    Text(
                        text = "Установленные игры (${calculateDiskUsed(formData.installedGames)}/${formData.diskTotal} ГБ)",
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(games) { game ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onGameToggle(game.id) }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = formData.installedGames.contains(game.id),
                                        onCheckedChange = { onGameToggle(game.id) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${game.name} (${game.diskSpace} ГБ)",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit) {
                Text(if (isEditing) "Сохранить" else "Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
