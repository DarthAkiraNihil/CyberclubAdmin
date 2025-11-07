package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId

data class Game(
    val id: String,
    val name: String,
    val description: String,
    val coverUrl: String,
    val diskSpace: Int
): Mappable, WithId {

    constructor(): this("", "", "", "", 0)

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "name" to this.name,
            "description" to this.description,
            "coverUrl" to this.coverUrl,
            "diskSpace" to this.diskSpace,
        )
    }
}
