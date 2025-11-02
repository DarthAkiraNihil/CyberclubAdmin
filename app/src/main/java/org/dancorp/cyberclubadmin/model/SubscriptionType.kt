package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId

data class SubscriptionType(
    val id: String,
    val name: String,
    val pricePerMonth: Double,
    val tariffCoefficient: Double
): Mappable, WithId {

    constructor(): this("", "", 0.0, 0.0)

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "name" to this.name,
            "pricePerMonth" to this.pricePerMonth,
            "tariffCoefficient" to this.tariffCoefficient,
        )
    }

}