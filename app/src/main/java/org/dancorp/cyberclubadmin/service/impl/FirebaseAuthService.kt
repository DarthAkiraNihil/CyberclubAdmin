package org.dancorp.cyberclubadmin.service.impl

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractAuthService
import org.dancorp.cyberclubadmin.service.AbstractUserService
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject
import java.util.Date

class FirebaseAuthService(firebase: Firebase, private val userService: AbstractUserService): AbstractAuthService {

    companion object {
        const val PASSWORDS_DO_NOT_MATCH = "Пароли не совпадают"
    }

    private val auth = firebase.auth

    override fun signIn(
        email: String,
        password: String,
        onSuccess: (User?) -> Unit
    ) {

        CoroutineScope(Dispatchers.IO).async {
            Log.i("app", "Signing in with email=$email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.i("app", "Retrieved firebase account data for $email")
            val user = userService.findByEmail(result.user!!.email!!)
            Log.v("app", "Got user: $user")
            onSuccess(user)
            Log.v("app", "onSuccess has been called")
        }
    }



    override suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ): ResultStateWithObject<User> {
        Log.i("app", "Creating user with data: email=$email, password=$password, confirm=$confirmPassword")
        if (password != confirmPassword) {
            return ResultStateWithObject(ok = false, PASSWORDS_DO_NOT_MATCH)
        }

        if (password.length < 6) {
            return ResultStateWithObject(ok = false, "Пароль должен содержать минимум 6 символов")
        }

        val user = this.userService.findByEmail(email)
        if (user != null) {
            return ResultStateWithObject(ok = false, "Пользователь с таким email уже существует")
        }

        this
            .auth
            .createUserWithEmailAndPassword(email, password)
            //.await()

//        if (!result.isSuccessful) {
//            return ResultStateWithObject(ok = false, "Что-то пошло не так")
//        }

        val hasVerifiedUsers = !this.userService.hasVerifiedUsers()
        val newUser = User(
            id = System.currentTimeMillis().toString(),
            email = email,
            verified = hasVerifiedUsers,
            verifiedBy = null,
            createdAt = Date()
        )

        this.userService.create(newUser)

        if (!hasVerifiedUsers) {
            return ResultStateWithObject(ok = true, "Регистрация успешна! Вы первый пользователь и автоматически подтверждены.", newUser)
        }

        return ResultStateWithObject(ok = true, "Регистрация успешна! Ожидайте подтверждения от администратора.", newUser)

    }

    override suspend fun verify(email: String, verifier: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun revoke(email: String): Boolean {
        TODO("Not yet implemented")
    }
}