package com.pandacorp.memocards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pandacorp.memocards.database.Card
import com.pandacorp.memocards.database.CardDatabase
import com.pandacorp.memocards.database.CardRepository
import com.pandacorp.memocards.database.CardStatus
import com.pandacorp.memocards.ui.theme.MemoCardsTheme
import com.pandacorp.memocards.viewmodel.HomeViewModel
import com.pandacorp.memocards.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartClick: () -> Unit,
    onAddCardClick: () -> Unit
) {
    val cardCounts by viewModel.cardCounts.collectAsState()
    var isCardListExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredCards by viewModel.getFilteredCards(searchQuery).collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color(0xFF0f1418),
        contentColor = Color.White
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
                    .padding(horizontal = 32.dp)
                    .height(50.dp)
                    .clip(MaterialTheme.shapes.medium),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF188fc0))
            ) {
                Text("START", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    placeholder = { Text("Search cards", color = Color.White.copy(alpha = 0.6f)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color(0xFF188ebf),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                IconButton(
                    onClick = { isCardListExpanded = !isCardListExpanded },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF188ebf), shape = MaterialTheme.shapes.medium)
                ) {
                    Icon(
                        imageVector = if (isCardListExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Toggle card list",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                FloatingActionButton(
                    onClick = onAddCardClick,
                    containerColor = Color(0xFF188ebf),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "Add Card",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            AnimatedVisibility(
                visible = isCardListExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                CardList(cards = filteredCards)
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatusCard(modifier = Modifier.weight(1f), count = toLearn, label = "Learning", color = Color(0xFF41af41))
        StatusCard(modifier = Modifier.weight(1f), count = known, label = "Known", color = Color(0xFF2795c6))
        StatusCard(modifier = Modifier.weight(1f), count = learned, label = "Learned", color = Color(0xFFdfb63a))
    }
}

@Composable
fun StatusCard(modifier: Modifier, count: Int, label: String, color: Color) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0d0d0d))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CardList(cards: List<Card>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(cards) { card ->
            CardItem(card)
        }
    }
}

@Composable
fun CardItem(card: Card) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1e2428))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (card.status) {
                    CardStatus.TO_LEARN -> Icons.Filled.Add
                    CardStatus.KNOWN -> Icons.Filled.Check
                    CardStatus.LEARNED -> Icons.Filled.Star
                },
                contentDescription = null,
                tint = when (card.status) {
                    CardStatus.TO_LEARN -> Color(0xFF41af41)
                    CardStatus.KNOWN -> Color(0xFF2795c6)
                    CardStatus.LEARNED -> Color(0xFFdfb63a)
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = card.front, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = card.back, color = Color.LightGray)
                if (card.details.isNotBlank()) {
                    Text(text = card.details, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Composable
fun HomeScreenPreview() {
    val database = CardDatabase.getDatabase(LocalContext.current)
    val repository = CardRepository(database.cardDao())

    val viewModelFactory = HomeViewModelFactory(repository)
    MemoCardsTheme {
        HomeScreen(ViewModelProvider(ViewModelStore(), viewModelFactory)[HomeViewModel::class.java], {}, {})
    }
}