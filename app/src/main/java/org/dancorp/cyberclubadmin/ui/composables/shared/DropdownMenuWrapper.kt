package org.dancorp.cyberclubadmin.ui.composables.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color

data class DropdownItem(
    val value: String,
    val label: String,
    val enabled: Boolean = true
)

@Composable
fun DropdownMenuWrapper(
    items: List<DropdownItem>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = items.find { it.value == selectedValue }?.label ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text(placeholder) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }
        )

        // Invisible clickable surface
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable { expanded = true }
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    onValueSelected(item.value)
                    expanded = false
                },
                enabled = item.enabled,
                text = {
                    Text(
                        text = item.label,
                        color = if (item.enabled) Color.Unspecified else Color.Gray
                    )
                }
            )
        }
    }
}
