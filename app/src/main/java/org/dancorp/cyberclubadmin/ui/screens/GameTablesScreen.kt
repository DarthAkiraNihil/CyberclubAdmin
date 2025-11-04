package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.service.AbstractGameService
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.ui.composables.table.GameTableCard
import org.dancorp.cyberclubadmin.ui.composables.table.GameTableDialog
import org.dancorp.cyberclubadmin.ui.composables.table.GameTableFormData
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun GameTablesScreen(
    parentActivity: Activity,
    gameService: AbstractGameService,
    gameTableService: AbstractGameTableService
) {
    var tables by remember { mutableStateOf(emptyList<GameTable>()) }
    var games by remember { mutableStateOf(emptyList<Game>()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<GameTable?>(null) }

    var formData by remember {
        mutableStateOf(GameTableFormData())
    }

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            tables = gameTableService.list()
            games = gameService.list()
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun resetForm() {
        formData = GameTableFormData(number = tables.size + 1)
        editingTable = null
    }

    fun handleEdit(table: GameTable) {
        editingTable = table
        formData = GameTableFormData(
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
        CoroutineScope(Dispatchers.IO).async {
            gameTableService.delete(tableId)
            parentActivity.runOnUiThread {
                Toast.makeText(context, "Стол удален", Toast.LENGTH_SHORT).show()
            }
            loadData()
        }
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

        CoroutineScope(Dispatchers.IO).async {

            val allTables = gameTableService.list().toMutableList()

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
                    CoroutineScope(Dispatchers.IO).async {

                        gameTableService.update(allTables[index].id, allTables[index])
                        parentActivity.runOnUiThread {
                            Toast.makeText(context, "Стол обновлен", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                if (allTables.any { it.number == formData.number }) {
                    Toast.makeText(context, "Стол с таким номером уже существует", Toast.LENGTH_SHORT).show()
                    return@async
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

                CoroutineScope(Dispatchers.IO).async {
                    gameTableService.create(newTable)
                    allTables.add(newTable)
                    parentActivity.runOnUiThread {
                        Toast.makeText(context, "Стол добавлен", Toast.LENGTH_SHORT).show()
                    }
                }

            }

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
                    GameTableCard(
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
        GameTableDialog(
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


