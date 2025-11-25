package org.dancorp.cyberclubadmin

import java.util.Date
import kotlin.math.ceil

fun getDaysUntilExpiry(expiryDate: Date): Double {
    val now = Date()
    val diff = expiryDate.time - now.time
    return ceil(diff.toDouble() / (1000 * 60 * 60 * 24))
}

fun getMinutesLeft(until: Date): Int {
    return ceil((until.time - Date().time) / 60_000.0).toInt()
}