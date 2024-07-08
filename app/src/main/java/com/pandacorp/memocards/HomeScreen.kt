package com.pandacorp.memocards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandacorp.memocards.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartClick: () -> Unit,
    onAddCardClick: () -> Unit
) {
    val cardCounts by viewModel.cardCounts.collectAsState()
    Scaffold(
        containerColor = Color(0xFF0f1418),
        contentColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCardClick,
                modifier = Modifier.padding(bottom = 46.dp, end = 32.dp),
                containerColor = Color(0xFF188ebf),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Add Card", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ProgressCards(
                toLearn = cardCounts.toLearn,
                known = cardCounts.known,
                learned = cardCounts.learned
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 200.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF188fc0))
            ) {
                Text("START", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProgressCards(toLearn: Int, known: Int, learned: Int) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatusCard(count = toLearn, label = "Learning", color = Color(0xFF41af41))
        StatusCard(count = known, label = "Known", color = Color(0xFF2795c6))
        StatusCard(count = learned, label = "Learned", color = Color(0xFFdfb63a))
    }
}

@Composable
fun StatusCard(count: Int, label: String, color: Color) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0d0d0d))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = color
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
    }
}