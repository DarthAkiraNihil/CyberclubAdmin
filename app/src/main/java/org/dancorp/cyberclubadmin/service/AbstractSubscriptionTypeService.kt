package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.SubscriptionType
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractSubscriptionTypeService: CrudService<SubscriptionType> {

    suspend fun create(name: String, pricePerMonth: Double, tariffCoefficient: Double): ResultStateWithObject<SubscriptionType>

}