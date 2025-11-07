package org.dancorp.cyberclubadmin.ui.composables.shared

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Отмена")
                }
            },
            onDismissRequest = onDismiss // Handles dismissing by tapping outside or back button
        )
    }
}