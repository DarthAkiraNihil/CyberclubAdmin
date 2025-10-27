package org.dancorp.cyberclubadmin.service.impl

import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractUserService

class UserService(private val repo: AbstractRepository<User>): AbstractUserService {

    override suspend fun get(id: Int): User? {
        return this.repo.get(id)
    }

    override suspend fun list(): List<User> {
        return this.repo.list()
    }

    override suspend fun create(obj: User) {
        this.repo.create(obj)
    }

    override suspend fun update(id: Int, updated: User) {
        this.repo.update(id, updated)
    }

    override suspend fun delete(id: Int) {
        this.repo.delete(id)
    }

    override suspend fun findByEmail(email: String): User? {
        return this.list().find { u: User -> u.email == email }
    }

    override suspend fun hasVerifiedUsers(): Boolean {
        return this.list().any { u: User -> u.verified }
    }
}