package org.dancorp.cyberclubadmin.service.impl

import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.model.Session
import org.dancorp.cyberclubadmin.service.AbstractGameService
import org.dancorp.cyberclubadmin.service.AbstractGameTableService
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

class GameTableService(
    private val gameTableRepo: AbstractRepository<GameTable>,
    private val sessionsRepo: AbstractRepository<Session>,
    private val gameService: AbstractGameService
): AbstractGameTableService {

    companion object {

        private const val ERROR_TABLE_WITH_NUMBER_ALREADY_EXISTS = "Стол с таким номером уже существует"
        private const val TABLE_HAS_BEEN_ADDED = "Стол добавлен"
        private const val TABLE_HAS_BEEN_UPDATED = "Стол обновлён"

    }


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

    override suspend fun create(
        number: Int,
        cpu: String,
        ram: Int,
        diskTotal: Int,
        gpu: String,
        hourlyRate: Int,
        installedGames: List<Game>
    ): ResultStateWithObject<GameTable> {

        val check = this.validateTable(cpu, gpu, diskTotal, installedGames)
        if (!check.ok) {
            return ResultStateWithObject(ok = false, check.message)
        }
        val allTables = this.list()
        if (allTables.any { it.number == number }) {
            return ResultStateWithObject(ok = false, ERROR_TABLE_WITH_NUMBER_ALREADY_EXISTS)
        }
        val gameIds = installedGames.map { it.id }

        val newTable = GameTable(
            id = System.currentTimeMillis().toString(),
            number = number,
            cpu = cpu,
            ram = ram,
            diskTotal = diskTotal,
            diskUsed = this.gameService.calculateDiskUsed(gameIds, installedGames),
            gpu = gpu,
            hourlyRate = hourlyRate,
            installedGames = gameIds
        )

        this.create(newTable)
        return ResultStateWithObject(ok = true, TABLE_HAS_BEEN_ADDED, newTable)
    }

    override suspend fun update(
        table: GameTable,
        cpu: String,
        ram: Int,
        diskTotal: Int,
        gpu: String,
        hourlyRate: Int,
        installedGames: List<Game>
    ): ResultStateWithObject<GameTable> {

        val check = this.validateTable(cpu, gpu, diskTotal, installedGames)
        if (!check.ok) {
            return ResultStateWithObject(ok = false, check.message)
        }
        val gameIds = installedGames.map { it.id }
        val updatedTable = table.copy(
            cpu = cpu,
            ram = ram,
            diskTotal = diskTotal,
            diskUsed = this.gameService.calculateDiskUsed(gameIds, installedGames),
            gpu = gpu,
            hourlyRate = hourlyRate,
            installedGames = gameIds
        )
        this.update(table.id, updatedTable)
        return ResultStateWithObject(ok = true, TABLE_HAS_BEEN_UPDATED, updatedTable)
    }

    private fun validateTable(cpu: String, gpu: String, diskTotal: Int, installedGames: List<Game>): ResultState {
        if (cpu.isBlank() || gpu.isBlank()) {
            return ResultState(ok = false, "Заполните все поля")
        }

        val gameIds = installedGames.map { it.id }
        val diskUsed = this.gameService.calculateDiskUsed(gameIds, installedGames)
        if (diskUsed > diskTotal) {
            return ResultState(ok = false, "Занятое место ($diskUsed ГБ) превышает общий объём (${diskTotal} ГБ)")
        }
        return ResultState(ok = true)
    }
}