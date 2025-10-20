package org.dancorp.cyberclubadmin.model

import java.util.Date

data class User(
    val id: String,
    val email: String,
    val password: String,
    val isVerified: Boolean,
    val verifiedBy: String?,
    val createdAt: Date
)
