package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.getMinutesLeft
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.model.Subscription
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.service.AbstractNotificationService
import org.dancorp.cyberclubadmin.service.AbstractSessionService
import org.dancorp.cyberclubadmin.service.AbstractSubscriptionService
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject
import java.util.Calendar
import java.util.Date
import kotlin.math.min

class SessionService(
    private val repo: AbstractRepository<Session>,
    private val gameTableService: AbstractGameTableService,
    private val subscriptionService: AbstractSubscriptionService,
    private val notificationService: AbstractNotificationService,
): AbstractSessionService {

    companion object {

        private const val ERROR_TABLE_IS_IN_USE = "Этот стол уже занят"

    }

    override suspend fun get(id: String): Session? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<Session> {

        val cal = Calendar.getInstance()
        val all = this.repo.list()
        val result = mutableListOf<Session>()
        for (session in all) {
            cal.time = session.createdAt
            cal.add(Calendar.MINUTE, session.bookedMinutes)
            val minutesLeft = getMinutesLeft(cal.time)
            if (minutesLeft <= 0) {
                val fix = session.copy(active = false, remainingMinutes = 0)
                result.add(fix)
                this.update(session.id, fix)
                this.notificationService.notifySessionExpired(fix)
            } else {
                result.add(session.copy(remainingMinutes = minutesLeft))
            }
        }
        return result
    }

    override suspend fun create(obj: Session) {
        this.repo.create(obj)
    }

    override suspend fun create(
        sub: Subscription?,
        table: GameTable?,
        bookedHours: Int,
        payAsDebt: Boolean,
    ): ResultStateWithObject<Session> {

        if (sub == null || table == null) {
            return ResultStateWithObject(ok = false, "FYCK")
        }

        val (ok, message) = sub.canCreateSession()
        if (!ok) {
            return ResultStateWithObject(ok = false, message)
        }

        if (!this.gameTableService.isTableAvailable(table.id)) {
            return ResultStateWithObject(ok = false, ERROR_TABLE_IS_IN_USE)
        }

        val bookedMinutes = bookedHours * 60
        val basePrice = table.hourlyRate * bookedHours
        val finalPrice = basePrice * sub.type.tariffCoefficient

        val newSession = Session(
            id = System.currentTimeMillis().toString(),
            tableId = table.id,
            subscriptionId = sub.id,
            startTime = Date(),
            bookedMinutes = bookedMinutes,
            remainingMinutes = bookedMinutes,
            basePrice = basePrice.toDouble(),
            finalPrice = finalPrice,
            active = true,
            paidForDebt = payAsDebt,
            createdAt = Date()
        )
        this.create(newSession)

        if (payAsDebt) {
            this.subscriptionService.addDebt(sub, finalPrice)
        }

        return ResultStateWithObject(ok = true, "Сессия создана на столе ${table.number}", newSession)
    }

    override suspend fun update(id: String, updated: Session) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.repo.delete(id)
    }

    override suspend fun listActive(): List<Session> {
        return this.repo.list().filter { it.active }
    }

    override suspend fun extend(sessionId: String) {

        val session = this.get(sessionId)!!
        val table = this.gameTableService.get(session.tableId)!!
        val sub = this.subscriptionService.get(session.subscriptionId)!!

        val additionalMinutes = 60
        val additionalCost = (table.hourlyRate * 1) * sub.type.tariffCoefficient

        this.update(session.id,session.copy(
            bookedMinutes = session.bookedMinutes + additionalMinutes,
            remainingMinutes = session.remainingMinutes + additionalMinutes,
            finalPrice = session.finalPrice + additionalCost)
        )

    }

    override suspend fun end(sessionId: String): Double {

        val session = this.get(sessionId)!!
        val elapsed = (System.currentTimeMillis() - session.startTime.time) / 60000
        val actualMinutes = min(elapsed.toInt(), session.bookedMinutes)

        val table = this.gameTableService.get(session.tableId)!!
        val sub = this.subscriptionService.get(session.subscriptionId)!!

        val actualPrice = (table.hourlyRate * (actualMinutes / 60.0)) * sub.type.tariffCoefficient

        this.update(sessionId, session.copy(
            active = false,
            finalPrice = actualPrice,
            remainingMinutes = 0
        ))

        return actualPrice
    }
}