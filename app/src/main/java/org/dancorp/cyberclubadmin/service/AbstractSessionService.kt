package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractSessionService: CrudService<Session> {

    suspend fun listActive(): List<Session>
    suspend fun create(
        sub: Subscription?,
        table: GameTable?,
        bookedHours: Int,
        payAsDebt: Boolean,
    ): ResultStateWithObject<Session>

    suspend fun extend(sessionId: String)
    suspend fun end(sessionId: String): Double

}