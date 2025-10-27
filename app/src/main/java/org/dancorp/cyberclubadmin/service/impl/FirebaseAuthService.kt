package org.dancorp.cyberclubadmin.service.impl

import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractAuthService
import org.dancorp.cyberclubadmin.shared.ResultState
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject
import java.util.Date

class FirebaseAuthService(firebase: Firebase, private val userService: UserService): AbstractAuthService {

    companion object {
        const val PASSWORDS_DO_NOT_MATCH = "Пароли не совпадают"
    }

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
            password = password,
            isVerified = hasVerifiedUsers,
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