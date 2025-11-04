package org.dancorp.cyberclubadmin.ui.composables.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2

@Composable
fun GridLayout(
    items: List<Pair<String, String>>,
    textColor: Color = Color.Unspecified
) {
    Column {
        items.chunked(2).forEach { chunk ->
            Row(modifier = Modifier.fillMaxWidth()) {
                chunk.forEach { (label, value) ->
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.body1,
                            color = textColor
                        )
                    }
                }
            }
            if (chunk.size < 2) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}