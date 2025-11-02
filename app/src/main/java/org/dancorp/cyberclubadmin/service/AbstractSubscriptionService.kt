package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.shared.ResultState

interface AbstractSubscriptionService: CrudService<Subscription> {

    suspend fun listActive(): List<Subscription>
    suspend fun addDebt(subscription: Subscription, finalPrice: Double)

}