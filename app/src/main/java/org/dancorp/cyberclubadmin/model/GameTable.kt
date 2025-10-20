package org.dancorp.cyberclubadmin.model

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
)
