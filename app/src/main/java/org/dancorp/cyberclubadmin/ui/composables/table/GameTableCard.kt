package org.dancorp.cyberclubadmin.ui.composables.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h6


@Composable
fun GameTableCard(
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
                GameTableSpecificationRow(
                    icon = Icons.Default.Memory,
                    label = "Процессор:",
                    value = table.cpu
                )
                GameTableSpecificationRow(
                    icon = Icons.Default.Devices,
                    label = "Видеокарта:",
                    value = table.gpu
                )
                GameTableSpecificationRow(
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
