package org.dancorp.cyberclubadmin.service.impl

import android.widget.Toast
import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.service.AbstractGameService
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

class GameService(private val repo: AbstractRepository<Game>): AbstractGameService {

    companion object {

        private const val ERROR_NAME_AND_DESC_NOT_PROVIDED = "Заполните название и описание"
        private const val GAME_HAS_BEEN_ADDED = "Игра добавлена"

    }

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

    override suspend fun create(
        name: String,
        description: String,
        coverUrl: String,
        diskSpace: Int
    ): ResultStateWithObject<Game> {
        if (name.isBlank() || description.isBlank()) {
            return ResultStateWithObject(ok = false, ERROR_NAME_AND_DESC_NOT_PROVIDED)
        }

        val newGame = Game(
            id = System.currentTimeMillis().toString(),
            name = name,
            description = description,
            coverUrl = coverUrl,
            diskSpace = diskSpace
        )

        this.create(newGame)
        return ResultStateWithObject(ok = true, GAME_HAS_BEEN_ADDED, newGame)
    }

    override fun calculateDiskUsed(gameIds: List<String>, games: List<Game>): Int {
        return gameIds.sumOf { gameId ->
            games.find { it.id == gameId }?.diskSpace ?: 0
        }
    }
}