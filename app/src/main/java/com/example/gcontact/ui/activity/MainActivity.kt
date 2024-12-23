package com.example.gcontact.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme
import com.example.gcontact.ui.features.contact.AddContactScreen
import com.example.gcontact.ui.features.contact.ContactScreen
import com.example.gcontact.ui.features.contact.UpdateContactScreen
import com.example.gcontact.util.KEY_CONTACT_ARG
import com.example.gcontact.util.MyScreens
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Entry point of the app.
     * Sets up the main UI theme and navigation.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{true}

        CoroutineScope(
            Dispatchers.IO
        ).launch {
            delay(2000)
            splashScreen.setKeepOnScreenCondition{false}
        }

        setContent {
            AppTheme { // Apply app-wide theme
                Scaffold(
                    modifier = Modifier.fillMaxSize() // Fullscreen layout
                ) {
                    MainContactNavigation() // Initialize app navigation
                }
            }
        }
    }
}

@Composable
fun MainContactNavigation() {
    // Create a NavController for navigation between screens
    val navController = rememberNavController()

    // Define the navigation host and routes
    NavHost(
        navController = navController,
        startDestination = MyScreens.ContactScreen.route // Default screen
    ) {
        // Contact list screen
        composable(route = MyScreens.ContactScreen.route) {
            ContactScreen(navController)
        }

        // Add contact screen
        composable(route = MyScreens.AddContactScreen.route) {
            AddContactScreen(navController)
        }

        // Update contact screen, expects a contact ID as argument
        composable(
            route = "${MyScreens.UpdateContactScreen.route}/{$KEY_CONTACT_ARG}",
            arguments = listOf(
                navArgument(KEY_CONTACT_ARG) { type = NavType.IntType } // Contact ID as Int
            )
        ) {
            val contactId = it.arguments?.getInt(KEY_CONTACT_ARG) ?: 0 // Default ID is 0 if missing
            UpdateContactScreen(contactId, navController)
        }
    }
}
