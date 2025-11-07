package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionTypeService
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

class SubscriptionTypeService(private val repo: AbstractRepository<SubscriptionType>): AbstractSubscriptionTypeService {

    companion object {
        private const val ERROR_TARIFF_COEFFICIENT_OUT_OF_BOUNDS = "Коэффициент тарификации должен быть от 0 до 1"
        private const val TYPE_HAS_BEEN_CREATED = "Тип абонемента создан"
    }

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

    override suspend fun create(
        name: String,
        pricePerMonth: Double,
        tariffCoefficient: Double
    ): ResultStateWithObject<SubscriptionType> {
        if (tariffCoefficient <= 0 || tariffCoefficient > 1) {
            return ResultStateWithObject(ok = false, ERROR_TARIFF_COEFFICIENT_OUT_OF_BOUNDS)
        }

        val newType = SubscriptionType(
            System.currentTimeMillis().toString(),
            name,
            pricePerMonth,
            tariffCoefficient,
        )
        this.create(newType)
        return ResultStateWithObject(ok = true, TYPE_HAS_BEEN_CREATED)
    }
}