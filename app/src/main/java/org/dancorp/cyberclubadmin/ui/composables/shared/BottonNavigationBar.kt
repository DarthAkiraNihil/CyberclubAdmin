package org.dancorp.cyberclubadmin.ui.composables.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Screen {
    SESSIONS, TABLES, GAMES, SUBSCRIPTIONS, NOTIFICATIONS, USERS
}

data class NavigationItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

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