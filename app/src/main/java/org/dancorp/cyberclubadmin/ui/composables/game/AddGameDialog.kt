package org.dancorp.cyberclubadmin.ui.composables.game

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

