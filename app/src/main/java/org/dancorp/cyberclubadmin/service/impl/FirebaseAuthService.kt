package org.dancorp.cyberclubadmin.service.impl

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractAuthService
import org.dancorp.cyberclubadmin.service.AbstractUserService
import org.dancorp.cyberclubadmin.shared.ResultStateWithObject
import java.util.Date

class FirebaseAuthService(firebase: Firebase, private val userService: AbstractUserService): AbstractAuthService {

    companion object {
        private const val ERROR_PASSWORDS_DO_NOT_MATCH = "Пароли не совпадают"
        private const val ERROR_PASSWORD_IS_TOO_SHORT = "Пароль должен содержать минимум 6 символов"
        private const val ERROR_USER_ALREADY_EXISTS = "Пользователь с таким email уже существует"
        private const val ERROR_INVALID_CREDENTIALS = "Неверный email или пароль"
        private const val ERROR_USER_IS_NOT_VERIFIED = "Ваш аккаунт не подтвержден. Обратитесь к администратору для подтверждения вашей учётной записи"
        private const val ERROR_SOMETHING_WENT_WRONG = "Что-то пошло не так"
        private const val ERROR_VERIFICATION_REJECTED = "Запрос на подтверждение был отклонён. Данный пользователь не имеет права регистрироваться в системе"
        private const val SIGN_UP_SUCCESS_FIRST = "Регистрация успешна! Вы первый пользователь и автоматически подтверждены"
        private const val SIGN_UP_SUCCESS = "Регистрация успешна! Ожидайте подтверждения от администратора"
        private const val SIGN_IN_SUCCESS = "Вход выполнен успешно!"
    }

    private val auth = firebase.auth
    private var current: User? = null

    override fun signIn(
        email: String,
        password: String,
        handler: (ResultStateWithObject<User>) -> Unit
    ) {

        CoroutineScope(Dispatchers.IO).async {

            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                handler(ResultStateWithObject(ok = false, ERROR_INVALID_CREDENTIALS))
                return@async
            }

            val user = userService.findByEmail(result.user!!.email!!)!!
            if (user.revoked) {
                handler(ResultStateWithObject(ok = false, ERROR_INVALID_CREDENTIALS))
                return@async
            }

            if (!user.verified) {
                handler(ResultStateWithObject(ok = false, ERROR_USER_IS_NOT_VERIFIED))
                return@async
            }

            current = user
            handler(ResultStateWithObject(ok = true, SIGN_IN_SUCCESS, user))
        }
    }



    override fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        handler: (ResultStateWithObject<User>) -> Unit
    ) {
        if (password != confirmPassword) {
            handler(ResultStateWithObject(ok = false, ERROR_PASSWORDS_DO_NOT_MATCH))
            return
        }

        if (password.length < 6) {
            handler(ResultStateWithObject(ok = false, ERROR_PASSWORD_IS_TOO_SHORT))
            return
        }

        CoroutineScope(Dispatchers.IO).async {
            val user: User? = userService.findByEmail(email)
            if (user != null && !user.revoked) {
                handler(ResultStateWithObject(ok = false, ERROR_USER_ALREADY_EXISTS))
            }

            if (user?.revoked == true) {
                handler(ResultStateWithObject(ok = false, ERROR_VERIFICATION_REJECTED))
            }

            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()
            if (result.user == null) {
                handler(ResultStateWithObject(ok = false, ERROR_SOMETHING_WENT_WRONG))
                return@async
            }

            val hasVerifiedUsers = userService.hasVerifiedUsers()
            val newUser = User(
                id = System.currentTimeMillis().toString(),
                email = email,
                verified = !hasVerifiedUsers,
                revoked = false,
                verifiedBy = null,
                createdAt = Date()
            )

            userService.create(newUser)

            if (!hasVerifiedUsers) {
                handler(ResultStateWithObject(ok = true, SIGN_UP_SUCCESS_FIRST, newUser))
                return@async
            }

            handler(ResultStateWithObject(ok = true, SIGN_UP_SUCCESS, newUser))
        }
    }

    override val currentUser: User?
        get() = this.current
}