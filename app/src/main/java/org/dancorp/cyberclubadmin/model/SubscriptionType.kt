package org.dancorp.cyberclubadmin.model

data class SubscriptionType(
    val id: String,
    val name: String,
    val pricePerMonth: Double,
    val tariffCoefficient: Double
)