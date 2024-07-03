package com.pandacorp.memocards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pandacorp.memocards.ui.theme.MemoCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoCardsTheme {
                // Note the use of Surface as a parent container
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0f1418)) {
                    HomeScreen(onStartClick={}, onAddCardClick={})
                }
            }
        }
    }
}