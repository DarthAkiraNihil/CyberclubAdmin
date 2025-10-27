package org.dancorp.cyberclubadmin.service.impl

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractAuthService

class FirebaseAuthService(firebase: Firebase, private val userService: UserService): AbstractAuthService {

    private val auth = firebase.auth

    override suspend fun signIn(
        email: String,
        password: String
    ): User? {
        val result = this
            .auth
            .signInWithEmailAndPassword(email, password)
            .await()
        if (result.user != null) {
            return this.userService.findByEmail(email)
        }

        return null
    }

    override suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val result = this
            .auth
            .createUserWithEmailAndPassword(email, password)
            .await()

        return result.user != null
    }

    override suspend fun verify(email: String, verifier: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun revoke(email: String): Boolean {
        TODO("Not yet implemented")
    }
}