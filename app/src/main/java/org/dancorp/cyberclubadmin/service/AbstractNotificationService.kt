package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.Notification
import org.dancorp.cyberclubadmin.model.Session

interface AbstractNotificationService: CrudService<Notification> {

    suspend fun notifySessionExpired(session: Session)

}