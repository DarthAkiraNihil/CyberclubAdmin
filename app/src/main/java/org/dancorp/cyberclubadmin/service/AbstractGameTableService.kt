package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.GameTable

interface AbstractGameTableService: CrudService<GameTable> {

    fun isTableAvailable(tableId: String): Boolean
    suspend fun listAvailableTables(): List<GameTable>

}