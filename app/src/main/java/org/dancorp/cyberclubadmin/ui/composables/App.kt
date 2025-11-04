package org.dancorp.cyberclubadmin.ui.composables

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.service.Services
import org.dancorp.cyberclubadmin.ui.composables.shared.BottomNavigationBar
import org.dancorp.cyberclubadmin.ui.composables.shared.Screen
import org.dancorp.cyberclubadmin.ui.screens.AuthScreen
import org.dancorp.cyberclubadmin.ui.screens.GameTablesScreen
import org.dancorp.cyberclubadmin.ui.screens.GamesScreen
import org.dancorp.cyberclubadmin.ui.screens.NotificationsScreen
import org.dancorp.cyberclubadmin.ui.screens.SessionsScreen
import org.dancorp.cyberclubadmin.ui.screens.SubscriptionsScreen
import org.dancorp.cyberclubadmin.ui.screens.UsersScreen
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.theme.subtitle2


@Composable
fun App(
    parentActivity: Activity,
    services: Services
) {
    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentScreen by remember { mutableStateOf(Screen.SESSIONS) }
    var unreadCount by remember { mutableIntStateOf(0) }

    LocalContext.current

    fun updateUnreadCount() {
        CoroutineScope(Dispatchers.IO).async {
            val notifications = services.notifications.list()
            val unread = notifications.count { !it.isRead }
            unreadCount = unread
        }
    }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).async {
            val user = services.auth.currentUser
            currentUser = user

            if (user != null) {
                updateUnreadCount()
            }
        }
    }



    fun handleLoginSuccess(user: User) {
        currentUser = user
    }

    fun handleLogout() {
        // Show confirmation dialog
        // Store.setCurrentUser(null)
        currentUser = null
        currentScreen = Screen.SESSIONS
    }

    if (currentUser == null) {
        AuthScreen(parentActivity = parentActivity, onLoginSuccess = { handleLoginSuccess(it) }, authService = services.auth)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Компьютерный Клуб",
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.subtitle2,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { handleLogout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                // colors = TopAppBarColors(containerColor = Color.White),
                //elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                unreadCount = unreadCount,
                onScreenChange = { screen ->
                    currentScreen = screen
                    if (screen == Screen.NOTIFICATIONS) {
                        // Update unread count after a delay
                    }
                }
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.SESSIONS -> SessionsScreen(
                    parentActivity,
                    sessionService = services.sessions,
                    gameTableService = services.gameTables,
                    subscriptionService = services.subscriptions,
                    notificationService = services.notifications
                )
                Screen.TABLES -> GameTablesScreen(
                    parentActivity,
                    gameService = services.games,
                    gameTableService = services.gameTables
                )
                Screen.GAMES -> GamesScreen(
                    parentActivity,
                    gameService = services.games,
                    gameTableService = services.gameTables
                )
                Screen.SUBSCRIPTIONS -> SubscriptionsScreen(
                    parentActivity,
                    subscriptionService = services.subscriptions,
                    subscriptionTypeService = services.subscriptionTypes,
                )
                Screen.NOTIFICATIONS -> NotificationsScreen(
                    parentActivity,
                    services.notifications,
                )
                Screen.USERS -> UsersScreen(
                    parentActivity,
                    services.auth,
                    services.users
                )
            }
        }
    }
}