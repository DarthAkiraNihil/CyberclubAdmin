package org.dancorp.cyberclubadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.dancorp.cyberclubadmin.data.Store
import org.dancorp.cyberclubadmin.ui.screens.AuthScreen
import org.dancorp.cyberclubadmin.ui.screens.SessionsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Store
        setContent {
            AdminClubApp()
        }
    }
}

@Composable
fun AdminClubApp() {
    val navController = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = "auth") {
            composable("auth") { AuthScreen(onLoginSuccess = { navController.navigate("main") }) }
            composable("main") { SessionsScreen() }
        }
    }
}

@Composable
fun MainScreen(x0: NavHostController) {
    TODO("Not yet implemented")
}
