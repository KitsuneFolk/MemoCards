package com.pandacorp.memocards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pandacorp.memocards.database.AddCardScreen
import com.pandacorp.memocards.database.CardDatabase
import com.pandacorp.memocards.database.CardEntity
import com.pandacorp.memocards.database.CardRepository
import com.pandacorp.memocards.ui.theme.MemoCardsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: CardDatabase
    private lateinit var repository: CardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = CardDatabase.getDatabase(this)
        repository = CardRepository(database.cardDao())

        enableEdgeToEdge()
        setContent {
            MemoCardsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0f1418)) {
                    AppNavigation(repository)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(repository: CardRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartClick = { navController.navigate("cards") },
                onAddCardClick = { navController.navigate("addCard") }
            )
        }
        composable("cards") {
            val cards by repository.allCards.collectAsState(initial = emptyList())
            CardsScreen(
                cards = cards.map { Card(it.front, it.back) },
                onClose = { navController.popBackStack() }
            )
        }
        composable("addCard") {
            AddCardScreen(
                onSaveCard = { front, back, details ->
                    // Use a coroutine scope to insert the card
                    CoroutineScope(Dispatchers.IO).launch {
                        //TODO: Add details
                        repository.insertCard(CardEntity(front = front, back = back))
                    }
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
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