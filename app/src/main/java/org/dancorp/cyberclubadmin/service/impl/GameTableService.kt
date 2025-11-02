package org.dancorp.cyberclubadmin.service.impl

import kotlinx.coroutines.runBlocking
import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.service.AbstractGameTableService

class GameTableService(
    private val gameTableRepo: AbstractRepository<GameTable>,
    private val sessionsRepo: AbstractRepository<Session>
): AbstractGameTableService {

    override suspend fun get(id: String): GameTable? {
        return this.gameTableRepo.get(id)
    }

    override suspend fun list(): List<GameTable> {
        return this.gameTableRepo.list()
    }

    override suspend fun create(obj: GameTable) {
        this.gameTableRepo.create(obj)
    }

    override suspend fun update(
        id: String,
        updated: GameTable
    ) {
        this.gameTableRepo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.gameTableRepo.delete(id)
    }

    override fun isTableAvailable(tableId: String): Boolean {
        return runBlocking {
            sessionsRepo.list().find {
                it.tableId == tableId && it.isActive
            } != null
        }
    }

    override suspend fun listAvailableTables(): List<GameTable> {
        return this.list().filter { this.isTableAvailable(it.id) }
    }

    override suspend fun anyHasGameInstalled(game: Game): Boolean {
        return this.list().any { table -> table.installedGames.contains(game.id) }
    }
}