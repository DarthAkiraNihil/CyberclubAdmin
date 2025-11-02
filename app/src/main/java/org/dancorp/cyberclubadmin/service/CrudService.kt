package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId

interface CrudService<T> where T: Mappable, T: WithId {

    suspend fun get(id: String): T?
    suspend fun list(): List<T>
    suspend fun create(obj: T)
    suspend fun update(id: String, updated: T)
    suspend fun delete(id: String)

}