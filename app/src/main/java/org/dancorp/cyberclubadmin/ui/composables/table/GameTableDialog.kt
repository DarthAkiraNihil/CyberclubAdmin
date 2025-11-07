package org.dancorp.cyberclubadmin.ui.composables.table

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.ui.theme.body2

@Composable
fun GameTableDialog(
    show: Boolean,
    games: List<Game>,
    editingTable: GameTable?,
    onDismiss: () -> Unit,
    onCreateGameTable: (Int, String, Int, Int, String, Int, List<Game>) -> Unit,
    onUpdateGameTable: (GameTable, String, Int, Int, String, Int, List<Game>) -> Unit,
    calculateDiskUsed: (List<String>, List<Game>) -> Int
) {
    Log.i("app", "Mounting game table dialog. games = $games, table = $editingTable")

    var formTableNumber by remember { mutableStateOf("") }
    var formTableCpu by remember { mutableStateOf("") }
    var formTableRam by remember { mutableStateOf("16") }
    var formTableDiskTotal by remember { mutableStateOf("500") }
    var formTableGpu by remember { mutableStateOf( "") }
    var formTableHourlyRate by remember { mutableStateOf("100") }
    var formTableInstalledGames by remember { mutableStateOf(emptyList<String>()) }

    Log.v("app", "fd: ${editingTable?.number}")
    if (editingTable != null) {
        formTableNumber = editingTable.number.toString()
        formTableCpu = editingTable.cpu
        formTableRam = editingTable.ram.toString()
        formTableDiskTotal = editingTable.diskTotal.toString()
        formTableGpu = editingTable.gpu
        formTableHourlyRate = editingTable.hourlyRate.toString()
        formTableInstalledGames = editingTable.installedGames
    }

    fun handleGameToggle(gameId: String) {
        Log.v("app", "handle change: $gameId, b4: $formTableInstalledGames")
        if (formTableInstalledGames.contains(gameId)) {
            Log.v("app", "coto")
            formTableInstalledGames = formTableInstalledGames.filter { it != gameId }
        } else {
            formTableInstalledGames += gameId
        }
        Log.v("app", "handle change: $gameId, a4: $formTableInstalledGames")
    }

    Log.v("app", "fd re: $formTableNumber")

    fun resetForm() {
        formTableNumber = ""
        formTableCpu = ""
        formTableRam = ""
        formTableDiskTotal = ""
        formTableGpu = ""
        formTableHourlyRate = ""
        formTableInstalledGames = emptyList()
    }

    if (!show) {
        return
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            resetForm()
        },
        title = { Text(if (editingTable != null) "Редактировать стол" else "Добавить стол") },
        text = {
            Column {
                Text(
                    text = "Настройка параметров игрового компьютера",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = formTableNumber,
                    onValueChange = { formTableNumber = it },
                    label = { Text("Номер стола") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = editingTable == null
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formTableCpu,
                    onValueChange = { formTableCpu = it },
                    label = { Text("Процессор") },
                    placeholder = { Text("Intel Core i7-12700K") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formTableGpu,
                    onValueChange = { formTableGpu = it },
                    label = { Text("Видеокарта") },
                    placeholder = { Text("NVIDIA RTX 3070") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = formTableRam,
                        onValueChange = { formTableRam = it },
                        label = { Text("ОЗУ (ГБ)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = formTableDiskTotal,
                        onValueChange = { formTableDiskTotal = it },
                        label = { Text("Диск (ГБ)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formTableHourlyRate,
                    onValueChange = { formTableHourlyRate = it },
                    label = { Text("Цена (₽/час)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Games selection
                Column {
                    Text(
                        text = "Установленные игры (${calculateDiskUsed(formTableInstalledGames, games)}/${formTableDiskTotal} ГБ)",
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
                                        .clickable { handleGameToggle(game.id) }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = formTableInstalledGames.contains(game.id),
                                        onCheckedChange = { handleGameToggle(game.id) }
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
            Button(onClick = {

                val number = formTableNumber.toIntOrNull()!!
                val ram = formTableRam.toIntOrNull()!!
                val diskTotal = formTableDiskTotal.toIntOrNull()!!
                val hourlyRate = formTableHourlyRate.toIntOrNull()!!
                val gameObjects = formTableInstalledGames.map { gId ->
                    games.find { it.id == gId }!!
                }

                if (editingTable != null) {
                    onUpdateGameTable(
                        editingTable,
                        formTableCpu,
                        ram,
                        diskTotal,
                        formTableGpu,
                        hourlyRate,
                        gameObjects
                    )
                } else {
                    onCreateGameTable(
                        number,
                        formTableCpu,
                        ram,
                        diskTotal,
                        formTableGpu,
                        hourlyRate,
                        gameObjects
                    )
                }
                resetForm()
            }) {
                Text(if (editingTable != null) "Сохранить" else "Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                resetForm()
            }) {
                Text("Отмена")
            }
        }
    )
}
