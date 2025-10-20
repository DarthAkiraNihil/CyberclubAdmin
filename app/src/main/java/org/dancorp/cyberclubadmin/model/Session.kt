package org.dancorp.cyberclubadmin.model

import java.util.Date

data class Session(
    val id: String,
    val tableNumber: Int,
    val subscriptionId: String,
    val startTime: Date,
    val bookedMinutes: Int,
    val remainingMinutes: Int,
    val basePrice: Double,
    val finalPrice: Double,
    val isActive: Boolean,
    val isPaidForDebt: Boolean,
    val createdAt: Date
)