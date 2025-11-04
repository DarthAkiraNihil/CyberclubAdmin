package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.User

interface AbstractUserService: CrudService<User> {
    suspend fun verify(userId: String, verifier: User)
    suspend fun revoke(userId: String): Boolean
    suspend fun findByEmail(email: String): User?
    suspend fun hasVerifiedUsers(): Boolean
    suspend fun findVerifier(userId: String): User?

}