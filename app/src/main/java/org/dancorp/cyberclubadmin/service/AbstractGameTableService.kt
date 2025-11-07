package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.Game
import org.dancorp.cyberclubadmin.model.GameTable
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractGameTableService: CrudService<GameTable> {

    fun isTableAvailable(tableId: String): Boolean
    suspend fun listAvailableTables(): List<GameTable>
    suspend fun anyHasGameInstalled(game: Game): Boolean

    suspend fun create(number: Int, cpu: String, ram: Int, diskTotal: Int, gpu: String, hourlyRate: Int, installedGames: List<Game>): ResultStateWithObject<GameTable>
    suspend fun update(table: GameTable, cpu: String, ram: Int, diskTotal: Int, gpu: String, hourlyRate: Int, installedGames: List<Game>): ResultStateWithObject<GameTable>

}