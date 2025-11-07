package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId
import java.util.Date
import kotlin.Int

data class Session(
    val id: String,
    val tableId: String,
    val subscriptionId: String,
    val startTime: Date,
    val bookedMinutes: Int,
    val remainingMinutes: Int,
    val basePrice: Double,
    val finalPrice: Double,
    val active: Boolean,
    val paidForDebt: Boolean,
    val createdAt: Date

): Mappable, WithId {

    constructor(): this("", "", "", Date(), 0, 0, 0.0, 0.0, false, false, Date())

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "tableId" to this.tableId,
            "subscriptionId" to this.subscriptionId,
            "startTime" to this.startTime,
            "bookedMinutes" to this.bookedMinutes,
            "remainingMinutes" to this.remainingMinutes,
            "basePrice" to this.basePrice,
            "finalPrice" to this.finalPrice,
            "isActive" to this.active,
            "isPaidForDebt" to this.paidForDebt,
            "createdAt" to this.createdAt,
        )
    }
}