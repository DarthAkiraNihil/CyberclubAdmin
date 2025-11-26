package org.dancorp.cyberclubadmin.ui.screens

import android.app.Activity
import android.util.Log
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
import org.dancorp.cyberclubadmin.service.AbstractGameService
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.ui.composables.game.AddGameDialog
import org.dancorp.cyberclubadmin.ui.composables.game.GameCard
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard

@Composable
fun GamesScreen(
    parentActivity: Activity,
    gameService: AbstractGameService,
    gameTableService: AbstractGameTableService
) {
    var games by remember { mutableStateOf(emptyList<Game>()) }
    var isAddGameDialogOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun loadData() {
        CoroutineScope(Dispatchers.IO).async {
            games = gameService.list()
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun handleCreateGame(name: String, description: String, coverUrl: String, diskSpace: Int) {
        Log.v("app", "form data: $name, $description, $coverUrl, $diskSpace")
        CoroutineScope(Dispatchers.IO).async {

            val result = gameService.create(name, description, coverUrl, diskSpace)
            parentActivity.runOnUiThread {
                Toast
                    .makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
            loadData()

        }
    }

    fun handleDeleteGame(game: Game) {
        CoroutineScope(Dispatchers.IO).async {
            val isUsed = gameTableService.anyHasGameInstalled(game)

            if (isUsed) {
                parentActivity.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Нельзя удалить игру, установленную на столах",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@async
            }

            // Show confirmation dialog
            gameService.delete(game.id)
            parentActivity.runOnUiThread {
                Toast.makeText(context, "Игра удалена", Toast.LENGTH_SHORT)
                    .show()
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
                    text = "Библиотека игр",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Управление доступными играми",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { isAddGameDialogOpen = true },
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (games.isEmpty()) {
            AlertCard(
                message = "Нет игр в библиотеке. Добавьте первую игру."
            )
        } else {
            LazyColumn {
                items(games) { game ->
                    GameCard(
                        game = game,
                        onDelete = { handleDeleteGame(game) },
                        gameTableService = gameTableService
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    AddGameDialog(
        show = isAddGameDialogOpen,
        onDismiss = {
            isAddGameDialogOpen = false
        },
        onCreateGameTable = { n, d, c, ds ->
            handleCreateGame(n, d, c, ds)
            isAddGameDialogOpen = false
        }
    )
}
