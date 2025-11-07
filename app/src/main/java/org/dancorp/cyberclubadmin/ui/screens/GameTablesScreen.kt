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

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            tables = gameTableService.list()
            games = gameService.list()
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun handleCreateGameTable(number: Int, cpu: String, ram: Int, diskTotal: Int, gpu: String, hourlyRate: Int, installedGames: List<Game>) {

        CoroutineScope(Dispatchers.IO).async {

            isDialogOpen = false
            val result = gameTableService.create(number, cpu, ram, diskTotal, gpu, hourlyRate, installedGames)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()

        }

    }

    fun handleUpdateGameTable(table: GameTable, cpu: String, ram: Int, diskTotal: Int, gpu: String, hourlyRate: Int, installedGames: List<Game>) {

        CoroutineScope(Dispatchers.IO).async {

            isDialogOpen = false
            val result = gameTableService.update(table, cpu, ram, diskTotal, gpu, hourlyRate, installedGames)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            editingTable = null
            loadData()

        }

    }

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
                        onEdit = {
                            editingTable = table
                            isDialogOpen = true
                        },
                        onDelete = { handleDelete(table.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    GameTableDialog(
        show = isDialogOpen,
        games = games,
        editingTable = editingTable,
        onDismiss = {
            isDialogOpen = false
            editingTable = null
        },
        onCreateGameTable = ::handleCreateGameTable,
        onUpdateGameTable = ::handleUpdateGameTable,
        calculateDiskUsed = gameService::calculateDiskUsed
    )
}
