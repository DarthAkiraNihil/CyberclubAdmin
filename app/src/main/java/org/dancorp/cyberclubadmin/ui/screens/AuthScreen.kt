package org.dancorp.cyberclubadmin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.AbstractAuthService
import org.dancorp.cyberclubadmin.service.AbstractUserService
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h4
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.widgets.TabButton
import java.util.Date

private enum class AuthTab {
    SIGN_IN,
    SIGN_UP
}
@Composable
fun AuthScreen(
    authService: AbstractAuthService,
    userService: AbstractUserService,
    onLoginSuccess: (User) -> Unit
) {
    var signInEmail by remember { mutableStateOf("") }
    var signInPassword by remember { mutableStateOf("") }

    var signUpEmail by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpConfirmPassword by remember { mutableStateOf("") }

    var selectedTab by remember { mutableStateOf(AuthTab.SIGN_IN) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEFF6FF), Color.White)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF2563EB), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = "App Icon",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Компьютерный Клуб",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Панель администратора",
                style = MaterialTheme.typography.body1,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            ) {
                TabButton(
                    text = "Вход",
                    isSelected = selectedTab == AuthTab.SIGN_IN,
                    onClick = { selectedTab = AuthTab.SIGN_IN },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Регистрация",
                    isSelected = selectedTab == AuthTab.SIGN_UP,
                    onClick = { selectedTab = AuthTab.SIGN_UP },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                AuthTab.SIGN_IN -> SignInTab(
                    email = signInEmail,
                    password = signInPassword,
                    onEmailChange = { signInEmail = it },
                    onPasswordChange = { signInPassword = it },
                    onSignIn = {
                        val user = runBlocking { authService.signIn(signInEmail, signInPassword) }

                        if (user == null) {
                            Toast.makeText(context, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                            return@SignInTab
                        }

                        if (!user.isVerified) {
                            Toast.makeText(context, "Ваш аккаунт не подтвержден. Обратитесь к администратору.", Toast.LENGTH_SHORT).show()
                            return@SignInTab
                        }

                        Store.setCurrentUser(user)
                        Toast.makeText(context, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(user)
                    }
                )
                AuthTab.SIGN_UP -> SignUpTab(
                    email = signUpEmail,
                    password = signUpPassword,
                    confirmPassword = signUpConfirmPassword,
                    onEmailChange = { signUpEmail = it },
                    onPasswordChange = { signUpPassword = it },
                    onConfirmPasswordChange = { signUpConfirmPassword = it },
                    onSignUp = {
                        val result = runBlocking { authService.signUp(signUpEmail, signUpPassword, signUpConfirmPassword) }
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        if (!result.ok) {
                            return@SignUpTab
                        }

                        onLoginSuccess(result.obj!!)
                    }
                )
            }
        }
    }
}

@Composable
private fun SignInTab(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Вход в систему",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Введите email и пароль для доступа",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                placeholder = { Text("admin@club.ru") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти")
            }
        }
    }
}

@Composable
private fun SignUpTab(
    email: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Создайте новый аккаунт администратора",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                placeholder = { Text("admin@club.ru") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Подтвердите пароль") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться")
            }
        }
    }
}