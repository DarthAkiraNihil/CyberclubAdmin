package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.User

interface AbstractAuthService {

    suspend fun signIn(email: String, password: String): User?
    suspend fun signUp(email: String, password: String, confirmPassword: String): Boolean
    suspend fun verify(email: String, verifier: String): Boolean
    suspend fun revoke(email: String): Boolean;

}