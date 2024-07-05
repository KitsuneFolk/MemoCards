package com.pandacorp.memocards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pandacorp.memocards.ui.theme.MemoCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoCardsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0f1418)) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartClick = { navController.navigate("cards") },
                onAddCardClick = { /* Handle add card click */ }
            )
        }
        composable("cards") {
            val sampleCards = listOf(
                Card("Hello", "Bonjour"),
                Card("Goodbye", "Au revoir"),
                Card("Please", "S'il vous plaît"),
                Card("Thank you", "Merci")
            )
            CardsScreen(
                cards = sampleCards,
                onClose = { navController.popBackStack() }
            )
        }
    }
}