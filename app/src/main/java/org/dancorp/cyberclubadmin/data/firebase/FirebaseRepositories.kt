package org.dancorp.cyberclubadmin.data.firebase

import com.google.firebase.Firebase
import org.dancorp.cyberclubadmin.data.AbstractRepository
import org.dancorp.cyberclubadmin.data.Repositories
import org.dancorp.cyberclubadmin.model.Session

class FirebaseRepositories: Repositories {

    private val sessionsRepository: SessionsFirebaseRepository

    constructor(firebase: Firebase) {
        this.sessionsRepository = SessionsFirebaseRepository(firebase)
    }

    override fun sessions(): AbstractRepository<Session> {
        return this.sessionsRepository
    }
}