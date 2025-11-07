package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionService
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject
import java.util.Calendar
import java.util.Date

class SubscriptionService(private val repo: AbstractRepository<Subscription>): AbstractSubscriptionService {

    companion object {

        const val ERROR_ENTER_CORRECT_DEBT_AMOUNT = "Введите корректную сумму"
        const val ERROR_EMAIL_IS_INCORRECT = "Заполните все поля"
        const val ERROR_SUBSCRIPTION_ALREADY_EXISTS = "У этого email уже есть активный абонемент"
        const val SUB_HAS_BEEN_EXTENDED = "Абонемент продлен на 1 месяц"
        const val SUB_HAS_BEEN_REVOKED = "Абонемент отозван"
        const val SUB_HAS_BEEN_CREATED = "Абонемент создан"
    }

    override suspend fun get(id: String): Subscription? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<Subscription> {
        return this.repo.list()
    }

    override suspend fun create(obj: Subscription) {
        this.repo.create(obj)
    }

    override suspend fun update(
        id: String,
        updated: Subscription
    ) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.repo.delete(id)
    }

    override suspend fun create(
        email: String,
        type: SubscriptionType
    ): ResultStateWithObject<Subscription> {

        val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$")
        if (email.isBlank() || !emailRegex.matches(email)) {
            return ResultStateWithObject(ok = false, ERROR_EMAIL_IS_INCORRECT)
        }

        val allSubscriptions = this.list()
        val existingActive = allSubscriptions.find {
            it.email == email && it.active
        }

        if (existingActive != null) {
            return ResultStateWithObject(ok = false, ERROR_SUBSCRIPTION_ALREADY_EXISTS)
        }

        val purchaseDate = Date()
        val expiryDate = Calendar.getInstance().apply {
            time = purchaseDate
            add(Calendar.MONTH, 1)
        }.time

        val newSubscription = Subscription(
            id = System.currentTimeMillis().toString(),
            subscriptionNumber = "SUB-${System.currentTimeMillis().toString().takeLast(6)}",
            email = email,
            type = type,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            debt = 0.0,
            unpaidSessions = 0,
            active = true
        )

        this.create(newSubscription)
        return ResultStateWithObject(ok = true, SUB_HAS_BEEN_CREATED, newSubscription)
    }

    override suspend fun listActive(): List<Subscription> {
        return this.repo.list().filter { it.active }
    }

    override suspend fun addDebt(subscription: Subscription, finalPrice: Double) {
        this.update(
            subscription.id,
            subscription.copy(
                debt = subscription.debt + finalPrice,
                unpaidSessions = subscription.unpaidSessions + 1
            )
        )
    }

    override suspend fun payDebt(subscription: Subscription, paidDebtAmount: Double): ResultState {
        if (paidDebtAmount <= 0 || paidDebtAmount > subscription.debt) {
            return ResultState(ok = false, ERROR_ENTER_CORRECT_DEBT_AMOUNT)
        }

        val updated = subscription.copy(
            debt = if (subscription.debt - paidDebtAmount < 0) 0.0 else subscription.debt - paidDebtAmount
        )
        this.repo.update(subscription.id, updated)
        return ResultState(ok = true, "Оплачено $paidDebtAmount ₽")
    }

    override suspend fun extendSubscription(subscription: Subscription): ResultState {
        val cal = Calendar.getInstance()
        cal.time = subscription.expiryDate
        cal.add(Calendar.MONTH, 1)
        val extended = subscription.copy(expiryDate = cal.time)
        this.repo.update(subscription.id, extended)
        return ResultState(ok = true, SUB_HAS_BEEN_EXTENDED)
    }

    override suspend fun revokeSubscription(subscription: Subscription): ResultState {
        this.repo.delete(subscription.id)
        return ResultState(ok = true, SUB_HAS_BEEN_REVOKED)
    }
}