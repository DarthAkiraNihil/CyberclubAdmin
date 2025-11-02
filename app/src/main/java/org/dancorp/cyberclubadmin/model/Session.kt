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
    val isActive: Boolean,
    val isPaidForDebt: Boolean,
    val createdAt: Date

): Mappable, WithId {

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
            "isActive" to this.isActive,
            "isPaidForDebt" to this.isPaidForDebt,
            "createdAt" to this.createdAt,
        )
    }
}