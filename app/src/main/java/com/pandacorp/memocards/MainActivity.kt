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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pandacorp.memocards.database.CardDatabase
import com.pandacorp.memocards.database.CardEntity
import com.pandacorp.memocards.database.CardRepository
import com.pandacorp.memocards.ui.theme.MemoCardsTheme
import com.pandacorp.memocards.viewmodel.HomeViewModel
import com.pandacorp.memocards.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: CardDatabase
    private lateinit var repository: CardRepository
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = CardDatabase.getDatabase(this)
        repository = CardRepository(database.cardDao())

        viewModelFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MemoCardsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0f1418)) {
                    AppNavigation(repository, homeViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(repository: CardRepository, homeViewModel: HomeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onStartClick = { navController.navigate("cards") },
                onAddCardClick = { navController.navigate("addCard") }
            )
        }
        composable("cards") {
            val cards by repository.allCards.collectAsState(initial = emptyList())
            CardsScreen(
                cardItems = cards,
                onClose = { navController.popBackStack() }
            )
        }
        composable("addCard") {
            AddCardScreen(
                onSaveCard = { front, back, details, status ->
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.insertCard(CardEntity(front = front, back = back, details = details, status = status))
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}