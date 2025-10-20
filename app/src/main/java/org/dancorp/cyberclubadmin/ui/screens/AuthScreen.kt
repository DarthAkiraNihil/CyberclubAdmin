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
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.ui.theme.body1
import org.dancorp.cyberclubadmin.ui.theme.body2
import org.dancorp.cyberclubadmin.ui.theme.h4
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.widgets.TabButton
import java.util.Date

@Composable
fun AuthScreen(
    onLoginSuccess: (User) -> Unit
) {
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("login") }

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
                    isSelected = selectedTab == "login",
                    onClick = { selectedTab = "login" },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Регистрация",
                    isSelected = selectedTab == "register",
                    onClick = { selectedTab = "register" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                "login" -> LoginTab(
                    email = loginEmail,
                    password = loginPassword,
                    onEmailChange = { loginEmail = it },
                    onPasswordChange = { loginPassword = it },
                    onLogin = {
                        val users = Store.getUsers()
                        val user = users.find { it.email == loginEmail && it.password == loginPassword }

                        if (user == null) {
                            Toast.makeText(context, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                            return@LoginTab
                        }

                        if (!user.isVerified) {
                            Toast.makeText(context, "Ваш аккаунт не подтвержден. Обратитесь к администратору.", Toast.LENGTH_SHORT).show()
                            return@LoginTab
                        }

                        Store.setCurrentUser(user)
                        Toast.makeText(context, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(user)
                    }
                )
                "register" -> RegisterTab(
                    email = registerEmail,
                    password = registerPassword,
                    confirmPassword = confirmPassword,
                    onEmailChange = { registerEmail = it },
                    onPasswordChange = { registerPassword = it },
                    onConfirmPasswordChange = { confirmPassword = it },
                    onRegister = {
                        if (registerPassword != confirmPassword) {
                            Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                            return@RegisterTab
                        }

                        if (registerPassword.length < 6) {
                            Toast.makeText(context, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show()
                            return@RegisterTab
                        }

                        val users = Store.getUsers()
                        if (users.any { it.email == registerEmail }) {
                            Toast.makeText(context, "Пользователь с таким email уже существует", Toast.LENGTH_SHORT).show()
                            return@RegisterTab
                        }

                        val currentUser = Store.getCurrentUser()
                        val hasVerifiedUsers = users.any { it.isVerified }

                        val newUser = User(
                            id = System.currentTimeMillis().toString(),
                            email = registerEmail,
                            password = registerPassword,
                            isVerified = !hasVerifiedUsers || (currentUser?.isVerified ?: false),
                            verifiedBy = currentUser?.id,
                            createdAt = Date()
                        )

                        Store.saveUsers(users + newUser)

                        if (!hasVerifiedUsers) {
                            Toast.makeText(context, "Регистрация успешна! Вы первый пользователь и автоматически подтверждены.", Toast.LENGTH_SHORT).show()
                            Store.setCurrentUser(newUser)
                            onLoginSuccess(newUser)
                        } else if (newUser.isVerified) {
                            Toast.makeText(context, "Пользователь зарегистрирован и подтвержден!", Toast.LENGTH_SHORT).show()
                            registerEmail = ""
                            registerPassword = ""
                            confirmPassword = ""
                        } else {
                            Toast.makeText(context, "Регистрация успешна! Ожидайте подтверждения от администратора.", Toast.LENGTH_SHORT).show()
                            registerEmail = ""
                            registerPassword = ""
                            confirmPassword = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginTab(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
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
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти")
            }
        }
    }
}

@Composable
private fun RegisterTab(
    email: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit
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
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться")
            }
        }
    }
}