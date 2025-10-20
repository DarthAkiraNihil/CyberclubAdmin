package org.dancorp.cyberclubadmin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h5
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.widgets.AlertCard
import kotlin.collections.filter

@Composable
fun GamesScreen() {
    var games by remember { mutableStateOf(emptyList<Game>()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var formData by remember {
        mutableStateOf(GameFormData())
    }

    fun loadData() {
        games = Store.getGames()
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    fun resetForm() {
        formData = GameFormData()
    }

    val context = LocalContext.current

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
                onClick = { isDialogOpen = true },
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
                        onDelete = {
                            val tables = Store.getTables()
                            val isUsed = tables.any { table -> table.installedGames.contains(game.id) }

                            if (isUsed) {
                                Toast.makeText(context, "Нельзя удалить игру, установленную на столах", Toast.LENGTH_SHORT).show()
                                return@GameCard
                            }

                            // Show confirmation dialog
                            val allGames = Store.getGames().filter { it.id != game.id }
                            Store.saveGames(allGames)
                            Toast.makeText(context, "Игра удалена", Toast.LENGTH_SHORT).show()
                            loadData()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (isDialogOpen) {
        AddGameDialog(
            formData = formData,
            onFormDataChange = { formData = it },
            onDismiss = {
                isDialogOpen = false
                resetForm()
            },
            onSubmit = {
                if (formData.name.isBlank() || formData.description.isBlank()) {
                    Toast.makeText(context, "Заполните название и описание", Toast.LENGTH_SHORT).show()
                    return@AddGameDialog
                }

                val newGame = Game(
                    id = System.currentTimeMillis().toString(),
                    name = formData.name,
                    description = formData.description,
                    coverUrl = formData.coverUrl,
                    diskSpace = formData.diskSpace
                )

                val allGames = Store.getGames() + newGame
                Store.saveGames(allGames)

                Toast.makeText(context, "Игра добавлена", Toast.LENGTH_SHORT).show()
                isDialogOpen = false
                resetForm()
                loadData()
            }
        )
    }
}

@Composable
private fun GameCard(
    game: Game,
    onDelete: () -> Unit
) {
    val tables = Store.getTables()
    val installedCount = tables.count { it.installedGames.contains(game.id) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Game cover
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (game.coverUrl.isNotBlank()) {
                    // Load image from URL - you'd use Coil or Glide in real implementation
                    Box(modifier = Modifier.fillMaxSize())
                } else {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Game",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }

                Text(
                    text = game.description,
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${game.diskSpace} ГБ",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )

                    if (installedCount > 0) {
                        Text(
                            text = "Установлено: $installedCount ${if (installedCount == 1) "стол" else "стола"}",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddGameDialog(
    formData: GameFormData,
    onFormDataChange: (GameFormData) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить игру") },
        text = {
            Column {
                Text(
                    text = "Новая игра в библиотеку",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = formData.name,
                    onValueChange = { onFormDataChange(formData.copy(name = it)) },
                    label = { Text("Название") },
                    placeholder = { Text("Counter-Strike 2") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formData.description,
                    onValueChange = { onFormDataChange(formData.copy(description = it)) },
                    label = { Text("Описание") },
                    placeholder = { Text("Тактический шутер от первого лица...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formData.coverUrl,
                    onValueChange = { onFormDataChange(formData.copy(coverUrl = it)) },
                    label = { Text("URL обложки (опционально)") },
                    placeholder = { Text("https://example.com/cover.jpg") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formData.diskSpace.toString(),
                    onValueChange = {
                        val value = it.toIntOrNull() ?: 50
                        onFormDataChange(formData.copy(diskSpace = value))
                    },
                    label = { Text("Объём (ГБ)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = onSubmit) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

data class GameFormData(
    val name: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val diskSpace: Int = 50
)