package org.dancorp.cyberclubadmin.data

import org.dancorp.cyberclubadmin.util.Mappable
import org.dancorp.cyberclubadmin.util.WithId

interface AbstractRepository<T> where T: Mappable, T: WithId {

    suspend fun get(id: Int): T?
    suspend fun list(): List<T>
    suspend fun create(obj: T)
    suspend fun update(id: Int, updated: T)
    suspend fun delete(id: Int)

}