package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionTypeService

class SubscriptionTypeService(private val repo: AbstractRepository<SubscriptionType>): AbstractSubscriptionTypeService {

    override suspend fun get(id: String): SubscriptionType? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<SubscriptionType> {
        return this.repo.list()
    }

    override suspend fun create(obj: SubscriptionType) {
        this.repo.create(obj)
    }

    override suspend fun update(id: String, updated: SubscriptionType) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.repo.delete(id)
    }

}