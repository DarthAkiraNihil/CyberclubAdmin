package org.dancorp.cyberclubadmin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun GameTablesScreen() {
    var tables by remember { mutableStateOf(emptyList<GameTable>()) }
    var games by remember { mutableStateOf(emptyList<Game>()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<GameTable?>(null) }

    var formData by remember {
        mutableStateOf(TableFormData())
    }

    fun loadData() {
        tables = Store.getTables()
        games = Store.getGames()
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun resetForm() {
        formData = TableFormData(number = tables.size + 1)
        editingTable = null
    }

    fun handleEdit(table: GameTable) {
        editingTable = table
        formData = TableFormData(
            number = table.number,
            cpu = table.cpu,
            ram = table.ram,
            diskTotal = table.diskTotal,
            gpu = table.gpu,
            hourlyRate = table.hourlyRate,
            installedGames = table.installedGames.toMutableList()
        )
        isDialogOpen = true
    }

    val context = LocalContext.current

    fun handleDelete(tableId: String) {
        // Show confirmation dialog in real implementation
        val allTables = Store.getTables().filter { it.id != tableId }
        Store.saveTables(allTables)
        Toast.makeText(context, "Стол удален", Toast.LENGTH_SHORT).show()
        loadData()
    }

    fun calculateDiskUsed(gameIds: List<String>): Int {
        return gameIds.sumOf { gameId ->
            games.find { it.id == gameId }?.diskSpace ?: 0
        }
    }

    fun handleSubmit() {
        if (formData.cpu.isBlank() || formData.gpu.isBlank()) {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val diskUsed = calculateDiskUsed(formData.installedGames)
        if (diskUsed > formData.diskTotal) {
            Toast.makeText(context, "Занятое место ($diskUsed ГБ) превышает общий объём (${formData.diskTotal} ГБ)", Toast.LENGTH_SHORT).show()
            return
        }

        val allTables = Store.getTables().toMutableList()

        if (editingTable != null) {
            val index = allTables.indexOfFirst { it.id == editingTable!!.id }
            if (index != -1) {
                allTables[index] = editingTable!!.copy(
                    number = formData.number,
                    cpu = formData.cpu,
                    ram = formData.ram,
                    diskTotal = formData.diskTotal,
                    diskUsed = diskUsed,
                    gpu = formData.gpu,
                    hourlyRate = formData.hourlyRate,
                    installedGames = formData.installedGames
                )
                Store.saveTables(allTables)
                Toast.makeText(context, "Стол обновлен", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (allTables.any { it.number == formData.number }) {
                Toast.makeText(context, "Стол с таким номером уже существует", Toast.LENGTH_SHORT).show()
                return
            }

            val newTable = GameTable(
                id = System.currentTimeMillis().toString(),
                number = formData.number,
                cpu = formData.cpu,
                ram = formData.ram,
                diskTotal = formData.diskTotal,
                diskUsed = diskUsed,
                gpu = formData.gpu,
                hourlyRate = formData.hourlyRate,
                installedGames = formData.installedGames
            )
            allTables.add(newTable)
            Store.saveTables(allTables)
            Toast.makeText(context, "Стол добавлен", Toast.LENGTH_SHORT).show()
        }

        isDialogOpen = false
        resetForm()
        loadData()
    }

    fun handleGameToggle(gameId: String) {
        val newGames = if (formData.installedGames.contains(gameId)) {
            formData.installedGames.filter { it != gameId }
        } else {
            formData.installedGames + gameId
        }
        formData = formData.copy(installedGames = newGames.toMutableList())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Игровые столы",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Управление компьютерами",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { isDialogOpen = true },
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tables.isEmpty()) {
            AlertCard(message = "Нет столов. Добавьте первый игровой стол.")
        } else {
            LazyColumn {
                items(tables) { table ->
                    TableCard(
                        table = table,
                        games = games,
                        onEdit = { handleEdit(table) },
                        onDelete = { handleDelete(table.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (isDialogOpen) {
        TableDialog(
            formData = formData,
            games = games,
            isEditing = editingTable != null,
            onFormDataChange = { formData = it },
            onGameToggle = ::handleGameToggle,
            onDismiss = {
                isDialogOpen = false
                resetForm()
            },
            onSubmit = ::handleSubmit,
            calculateDiskUsed = ::calculateDiskUsed
        )
    }
}

@Composable
private fun TableCard(
    table: GameTable,
    games: List<Game>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val installedGames = table.installedGames.mapNotNull { id -> games.find { it.id == id } }
    val diskUsagePercent = (table.diskUsed.toDouble() / table.diskTotal) * 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Стол ${table.number}",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${table.hourlyRate} ₽/час",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specifications
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpecificationRow(
                    icon = Icons.Default.Memory,
                    label = "Процессор:",
                    value = table.cpu
                )
                SpecificationRow(
                    icon = Icons.Default.Devices,
                    label = "Видеокарта:",
                    value = table.gpu
                )
                SpecificationRow(
                    icon = Icons.Default.Storage,
                    label = "ОЗУ:",
                    value = "${table.ram} ГБ"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Disk usage
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Диск", style = MaterialTheme.typography.body2, color = Color.Gray)
                    Text("${table.diskUsed} / ${table.diskTotal} ГБ", style = MaterialTheme.typography.body2)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                progress = { (diskUsagePercent / 100f).toFloat() },
                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                color = when {
                                        diskUsagePercent > 90 -> Color.Red
                                        diskUsagePercent > 70 -> Color.Yellow
                                        else -> Color.Green
                                    },
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Installed games
            if (installedGames.isNotEmpty()) {
                Column {
                    Text(
                        text = "Установлено игр: ${installedGames.size}",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        installedGames.forEach { game ->
                            Badge(
                                containerColor = Color.LightGray.copy(alpha = 0.3f)
                            ) {
                                Text(game.name, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecificationRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.body2, color = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, style = MaterialTheme.typography.body2)
    }
}

@Composable
private fun TableDialog(
    formData: TableFormData,
    games: List<Game>,
    isEditing: Boolean,
    onFormDataChange: (TableFormData) -> Unit,
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

data class TableFormData(
    val number: Int = 1,
    val cpu: String = "",
    val ram: Int = 16,
    val diskTotal: Int = 500,
    val gpu: String = "",
    val hourlyRate: Int = 100,
    val installedGames: MutableList<String> = mutableListOf()
)