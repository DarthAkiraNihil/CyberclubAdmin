package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractUserService

class UserService(private val repo: AbstractRepository<User>): AbstractUserService {

    override suspend fun get(id: String): User? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<User> {
        return this.repo.list()
    }

    override suspend fun create(obj: User) {
        this.repo.create(obj)
    }

    override suspend fun update(id: String, updated: User) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: String) {
        this.repo.delete(id)
    }

    override suspend fun findVerifier(userId: String): User? {
        val user = this.repo.get(userId)
        if (user == null || user.verifiedBy == null) {
            return null
        }

        return this.repo.get(user.verifiedBy)
    }

    override suspend fun verify(userId: String, verifier: User) {
        val user = this.get(userId)!!
        this.update(
            userId,
            user.copy(
                verified = true,
                verifiedBy = verifier.id
            )
        )
    }

    override suspend fun revoke(userId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun findByEmail(email: String): User? {
        return this.list().find { u: User -> u.email == email }
    }

    override suspend fun hasVerifiedUsers(): Boolean {
        return this.list().any { u: User -> u.verified }
    }
}