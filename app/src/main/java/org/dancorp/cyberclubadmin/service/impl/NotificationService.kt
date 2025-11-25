package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.service.AbstractNotificationService
import java.util.Date

class NotificationService(private val repo: AbstractRepository<Notification>): AbstractNotificationService {

    companion object {

        private const val TYPE_SESSION_EXPIRED = "session_expired"

    }

    override suspend fun get(id: String): Notification? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<Notification> {
        return this.repo.list()
    }

    override suspend fun create(obj: Notification) {
        this.repo.create(obj)
    }

    override suspend fun update(
        id: String,
        updated: Notification
    ) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.repo.delete(id)
    }

    override suspend fun notifySessionExpired(session: Session) {
        this.create(
            Notification(
                id = System.currentTimeMillis().toString(),
                type = TYPE_SESSION_EXPIRED,
                message = "Время сессии на столе ${session.tableId} истекло!",
                timestamp = Date(),
                read = false,
                relatedId = session.id
            )
        )
    }
}