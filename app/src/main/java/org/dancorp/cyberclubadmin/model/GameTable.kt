package org.dancorp.cyberclubadmin.model

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId

data class GameTable(
    val id: String,
    val number: Int,
    val cpu: String,
    val ram: Int,
    val diskTotal: Int,
    val diskUsed: Int,
    val gpu: String,
    val hourlyRate: Int,
    val installedGames: List<String>
) : Mappable, WithId {

    constructor() : this(
        "",
        0,
        "",
        0,
        0,
        0,
        "",
        0,
        emptyList()
    )

    override fun id(): String {
        return this.id
    }

    override fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to this.id,
            "number" to this.number,
            "cpu" to this.cpu,
            "ram" to this.ram,
            "diskTotal" to this.diskTotal,
            "diskUsed" to this.diskUsed,
            "gpu" to this.gpu,
            "hourlyRate" to this.hourlyRate,
            "installedGames" to this.installedGames,
        )
    }
}
