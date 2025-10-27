package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractAuthService {

    fun signIn(email: String, password: String, onSuccess: (User?) -> Unit)
    suspend fun signUp(email: String, password: String, confirmPassword: String): ResultStateWithObject<User>
    suspend fun verify(email: String, verifier: String): Boolean
    suspend fun revoke(email: String): Boolean

}