package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.User

interface AbstractUserService: CrudService<User> {
    suspend fun findByEmail(email: String): User?
    suspend fun hasVerifiedUsers(): Boolean

}