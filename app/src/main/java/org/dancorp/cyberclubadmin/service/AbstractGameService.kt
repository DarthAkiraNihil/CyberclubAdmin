package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractGameService: CrudService<Game> {

    suspend fun create(name: String, description: String, coverUrl: String, diskSpace: Int): ResultStateWithObject<Game>
    fun calculateDiskUsed(gameIds: List<String>, games: List<Game>): Int

}