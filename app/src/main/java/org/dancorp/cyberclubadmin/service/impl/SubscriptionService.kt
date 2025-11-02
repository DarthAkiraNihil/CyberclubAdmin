package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionService
import org.dancorp.cyberclubadmin.shared.ResultState

class SubscriptionService(private val repo: AbstractRepository<Subscription>): AbstractSubscriptionService {

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

    override suspend fun listActive(): List<Subscription> {
        return this.repo.list().filter { it.isActive }
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
}