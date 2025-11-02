package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.service.AbstractGameService

class GameService(private val repo: AbstractRepository<Game>): AbstractGameService {

    override suspend fun get(id: String): Game? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<Game> {
        return this.repo.list()
    }

    override suspend fun create(obj: Game) {
        this.repo.create(obj)
    }

    override suspend fun update(id: String, updated: Game) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.repo.delete(id)
    }

}