package org.dancorp.cyberclubadmin.shared

data class ResultStateWithObject<T>(
    val ok: Boolean,
    val message: String = "",
    val obj: T? = null,
)