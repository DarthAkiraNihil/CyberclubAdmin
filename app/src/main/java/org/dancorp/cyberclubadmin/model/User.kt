package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId
import java.util.Date

data class User(
    val id: String,
    val email: String,
    val verified: Boolean,
    val verifiedBy: String?,
    val createdAt: Date
): Mappable, WithId {

    constructor(): this("", "", false, null, Date())

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "email" to this.email,
            "isVerified" to this.verified,
            "verifiedBy" to this.verifiedBy,
            "createdAt" to this.createdAt,
        )
    }
}
