package org.dancorp.cyberclubadmin.model

import java.util.Date

data class Subscription(
    val id: String,
    val subscriptionNumber: String,
    val email: String,
    val typeId: String,
    val purchaseDate: Date,
    val expiryDate: Date,
    val debt: Double,
    val unpaidSessions: Int,
    val isActive: Boolean
)
