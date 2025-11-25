package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.getDaysUntilExpiry
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId
import java.util.Date

data class Subscription(
    val id: String,
    val subscriptionNumber: String,
    val email: String,
    val type: SubscriptionType,
    val purchaseDate: Date,
    val expiryDate: Date,
    val debt: Double,
    val unpaidSessions: Int,
    val active: Boolean
): Mappable, WithId {

    companion object {

        private const val MAX_DEBT = 20000
        private const val MAX_UNPAID_SESSIONS = 3

        private const val ERROR_UNPAID_SESSION_LIMIT_REACHED = "Превышен лимит неоплаченных сессий ($MAX_UNPAID_SESSIONS)"
        private const val ERROR_DEBT_LIMIT_REACHED = "Сумма долга превышает $MAX_DEBT ₽"

    }

    constructor(): this("", "", "", SubscriptionType(), Date(), Date(), 0.0, 0, false)

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "subscriptionNumber" to this.subscriptionNumber,
            "email" to this.email,
            "type" to this.type.toMap(),
            "purchaseDate" to this.purchaseDate,
            "expiryDate" to this.expiryDate,
            "debt" to this.debt,
            "unpaidSessions" to this.unpaidSessions,
            "active" to this.active,
        )
    }

    fun canCreateSession(): ResultState {
        if (this.unpaidSessions >= MAX_UNPAID_SESSIONS) {
            return ResultState(false, ERROR_UNPAID_SESSION_LIMIT_REACHED)
        }
        if (this.debt > MAX_DEBT) {
            return ResultState(false, ERROR_DEBT_LIMIT_REACHED)
        }
        return ResultState(true)
    }

    fun isExpiringSoon(): Boolean {
        val daysLeft = getDaysUntilExpiry(this.expiryDate)
        return daysLeft <= 7 && daysLeft > 0
    }

    fun isExpired(): Boolean {
        val daysLeft = getDaysUntilExpiry(this.expiryDate)
        return daysLeft <= 0
    }

}
