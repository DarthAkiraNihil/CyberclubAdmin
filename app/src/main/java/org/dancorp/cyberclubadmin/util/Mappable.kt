package org.dancorp.cyberclubadmin.util

interface Mappable {
    fun toMap(): Map<String, Any?>
}