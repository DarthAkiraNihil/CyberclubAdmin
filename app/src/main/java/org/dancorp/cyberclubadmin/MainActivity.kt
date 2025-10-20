package org.dancorp.cyberclubadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.model.User
import org.dancorp.cyberclubadmin.ui.screens.AuthScreen
import org.dancorp.cyberclubadmin.ui.screens.GameTablesScreen
import org.dancorp.cyberclubadmin.ui.screens.GamesScreen
import org.dancorp.cyberclubadmin.ui.screens.NotificationsScreen
import org.dancorp.cyberclubadmin.ui.screens.SessionsScreen
import org.dancorp.cyberclubadmin.ui.screens.SubscriptionsScreen
import org.dancorp.cyberclubadmin.ui.screens.UsersScreen
import org.dancorp.cyberclubadmin.ui.theme.CyberclubAdminTheme
import org.dancorp.cyberclubadmin.ui.theme.h6
import org.dancorp.cyberclubadmin.ui.theme.subtitle2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberclubAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.SESSIONS) }
    var unreadCount by remember { mutableStateOf(0) }

    val context = LocalContext.current

    fun updateUnreadCount() {
        val notifications = Store.getNotifications()
        val unread = notifications.count { !it.isRead }
        unreadCount = unread
    }

    LaunchedEffect(Unit) {
        val user = Store.getCurrentUser()
        currentUser = user

        if (user != null) {
            updateUnreadCount()
        }
    }



    fun handleLoginSuccess(user: User) {
        currentUser = user
    }

    fun handleLogout() {
        // Show confirmation dialog
        Store.setCurrentUser(null)
        currentUser = null
        currentScreen = Screen.SESSIONS
    }

    if (currentUser == null) {
        AuthScreen(onLoginSuccess = { handleLoginSuccess(it) })
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
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
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
                Screen.SESSIONS -> SessionsScreen()
                Screen.TABLES -> GameTablesScreen()
                Screen.GAMES -> GamesScreen()
                Screen.SUBSCRIPTIONS -> SubscriptionsScreen()
                Screen.NOTIFICATIONS -> NotificationsScreen()
                Screen.USERS -> UsersScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    unreadCount: Int,
    onScreenChange: (Screen) -> Unit
) {
    val navigationItems = listOf(
        NavigationItem(Screen.SESSIONS, "Сессии", Icons.Default.PlayArrow),
        NavigationItem(Screen.TABLES, "Столы", Icons.Default.Computer),
        NavigationItem(Screen.GAMES, "Игры", Icons.Default.SportsEsports),
        NavigationItem(Screen.SUBSCRIPTIONS, "Абонементы", Icons.Default.CreditCard),
        NavigationItem(Screen.NOTIFICATIONS, "Уведомления", Icons.Default.Notifications),
        NavigationItem(Screen.USERS, "Админы", Icons.Default.People)
    )

    NavigationBar(
        containerColor = Color.White,
        // elevation = 8.dp
    ) {
        navigationItems.forEach { item ->
            val badgeCount = if (item.screen == Screen.NOTIFICATIONS) unreadCount else 0

            NavigationBarItem(
                selected = currentScreen == item.screen,
                onClick = { onScreenChange(item.screen) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge {
                                    Text(
                                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(item.label, fontSize = 12.sp) },
                // selectedContentColor = Color.Blue,
                // unselectedContentColor = Color.Gray,
                alwaysShowLabel = true
            )
        }
    }
}

enum class Screen {
    SESSIONS, TABLES, GAMES, SUBSCRIPTIONS, NOTIFICATIONS, USERS
}

data class NavigationItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)
