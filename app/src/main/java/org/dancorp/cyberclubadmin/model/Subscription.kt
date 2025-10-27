package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId
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
): Mappable, WithId {

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "subscriptionNumber" to this.subscriptionNumber,
            "email" to this.email,
            "typeId" to this.typeId,
            "purchaseDate" to this.purchaseDate,
            "expiryDate" to this.expiryDate,
            "debt" to this.debt,
            "unpaidSessions" to this.unpaidSessions,
            "isActive" to this.isActive,
        )
    }

}
