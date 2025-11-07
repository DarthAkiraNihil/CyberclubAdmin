package org.dancorp.cyberclubadmin.ui.composables.game

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.dancorp.cyberclubadmin.ui.theme.body2

data class GameFormData(
    val name: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val diskSpace: Int = 50
)

@Composable
fun AddGameDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onCreateGameTable: (String, String, String, Int) -> Unit
) {

    var formGameName by remember { mutableStateOf("") }
    var formGameDescription by remember { mutableStateOf("") }
    var formGameCoverUrl by remember { mutableStateOf("") }
    var formGameDiskSpace by remember { mutableStateOf("") }

    if (!show) {
        return
    }

    fun resetForm() {
        formGameName = ""
        formGameDescription = ""
        formGameCoverUrl = ""
        formGameDiskSpace = ""
    }

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
                    value = formGameName,
                    onValueChange = { formGameName = it },
                    label = { Text("Название") },
                    placeholder = { Text("Counter-Strike 2") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formGameDescription,
                    onValueChange = { formGameDescription = it },
                    label = { Text("Описание") },
                    placeholder = { Text("Тактический шутер от первого лица...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formGameCoverUrl,
                    onValueChange = { formGameCoverUrl = it },
                    label = { Text("URL обложки (опционально)") },
                    placeholder = { Text("https://example.com/cover.jpg") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = formGameDiskSpace,
                    onValueChange = { formGameDiskSpace = it },
                    label = { Text("Объём (ГБ)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                Log.i("app", "ds is $formGameDiskSpace")
                onCreateGameTable(
                    formGameName,
                    formGameDescription,
                    formGameCoverUrl,
                    formGameDiskSpace.toIntOrNull()!!
                )
                resetForm()
            }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                resetForm()
                onDismiss()
            }) {
                Text("Отмена")
            }
        }
    )
}

