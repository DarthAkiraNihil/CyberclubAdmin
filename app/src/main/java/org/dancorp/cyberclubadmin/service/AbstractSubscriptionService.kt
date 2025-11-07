package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractSubscriptionService: CrudService<Subscription> {
    suspend fun create(email: String, type: SubscriptionType): ResultStateWithObject<Subscription>
    suspend fun listActive(): List<Subscription>
    suspend fun addDebt(subscription: Subscription, finalPrice: Double)
    suspend fun payDebt(subscription: Subscription, paidDebtAmount: Double): ResultState
    suspend fun extendSubscription(subscription: Subscription): ResultState
    suspend fun revokeSubscription(subscription: Subscription): ResultState

}