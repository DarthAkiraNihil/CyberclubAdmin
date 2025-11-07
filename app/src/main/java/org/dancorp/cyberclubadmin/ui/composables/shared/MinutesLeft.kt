package org.dancorp.cyberclubadmin.ui.composables.shared

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Date
import kotlin.math.ceil

@Composable
fun MinutesLeft(until: Date): Int {
    var value by remember { mutableIntStateOf(getMinutesLeft(until)) }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable {
            value = getMinutesLeft(until)
        }

        handler.postDelayed(runnable, 60_000)

        onDispose {
            handler.removeCallbacks(runnable)
        }
    }

    return value
}

private fun getMinutesLeft(until: Date): Int {
    return ceil((until.time - Date().time) / 60_000.0).toInt()
}