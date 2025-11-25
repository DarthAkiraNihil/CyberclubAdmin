package org.dancorp.cyberclubadmin.service

import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject

interface AbstractAuthService {

    fun signIn(email: String, password: String, handler: (ResultStateWithObject<User>) -> Unit)
    fun signUp(email: String, password: String, confirmPassword: String, handler: (ResultStateWithObject<User>) -> Unit)
    fun signOut()
    val currentUser: User?

}