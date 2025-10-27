package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId
import java.util.Date

data class Notification(
    val id: String,
    val type: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean,
    val relatedId: String?
): Mappable, WithId {

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "type" to this.type,
            "message" to this.message,
            "timestamp" to this.timestamp,
            "isRead" to this.isRead,
            "relatedId" to this.relatedId,
        )
    }
}
