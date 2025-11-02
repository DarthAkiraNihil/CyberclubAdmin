package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.service.AbstractNotificationService

class NotificationService(private val repo: AbstractRepository<Notification>): AbstractNotificationService {

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
}