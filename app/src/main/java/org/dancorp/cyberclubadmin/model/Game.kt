package org.dancorp.cyberclubadmin.model

data class Game(
    val id: String,
    val name: String,
    val description: String,
    val coverUrl: String,
    val diskSpace: Int
)
