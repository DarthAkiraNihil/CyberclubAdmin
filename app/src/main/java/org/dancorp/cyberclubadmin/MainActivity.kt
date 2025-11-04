package org.dancorp.cyberclubadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.dancorp.cyberclubadmin.service.impl.DefaultServicesLoader
import org.dancorp.cyberclubadmin.ui.composables.App
import org.dancorp.cyberclubadmin.ui.theme.CyberclubAdminTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val services = DefaultServicesLoader().load()
        Log.i("app", "Loaded services: $services")

        setContent {
            CyberclubAdminTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(this, services)
                }
            }
        }
    }
}




