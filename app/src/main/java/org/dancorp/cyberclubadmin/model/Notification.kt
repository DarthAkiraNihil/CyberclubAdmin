package org.dancorp.cyberclubadmin.model

import java.util.Date

data class Notification(
    val id: String,
    val type: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean,
    val relatedId: String?
)
