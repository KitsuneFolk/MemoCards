package com.pandacorp.memocards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pandacorp.memocards.ui.theme.MemoCardsTheme
import kotlin.math.absoluteValue

@Composable
fun CardsScreen(cards: List<Card>, onClose: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(top = 20.dp, end = 20.dp)
                .align(Alignment.TopEnd)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        // Cards
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            cards.asReversed().take(3).reversed().forEachIndexed { index, card ->
                val cardIndex = currentIndex + index
                if (cardIndex < cards.size) {
                    key(cardIndex) {
                        SwipeableCard(
                            card = cards[cardIndex],
                            onSwiped = {
                                currentIndex++
                            },
                            isTopCard = index == 0,
                            modifier = Modifier
                                .padding(bottom = (index * 16).dp)
                                .zIndex(100f - index.toFloat())
                        )
                    }
                }
            }
        }

        // Completion message
        if (currentIndex >= cards.size) {
            Text(
                "All cards completed!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

private const val horizontalMinSwipeDistance = 50
private const val verticalMinSwipeDistance = 50

@Composable
fun SwipeableCard(
    card: Card,
    onSwiped: () -> Unit,
    isTopCard: Boolean,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val rotation by animateFloatAsState(targetValue = offsetX * 0.1f, label = "rotation")

    // Reset offset when the card becomes the top card
    LaunchedEffect(isTopCard) {
        if (isTopCard) {
            offsetX = 0f
            offsetY = 0f
        }
    }

    // Calculate alpha values based on swipe direction and distance
    val horizontalAlpha = (offsetX.absoluteValue / horizontalMinSwipeDistance).coerceIn(0f, 1f)
    val verticalAlpha = (offsetY.absoluteValue / verticalMinSwipeDistance).coerceIn(0f, 1f)

    val rightAlpha = if (offsetX > 0) horizontalAlpha * (1 - verticalAlpha) else 0f
    val leftAlpha = if (offsetX < 0) horizontalAlpha * (1 - verticalAlpha) else 0f
    val bottomAlpha = if (offsetY > 0) verticalAlpha * (1 - horizontalAlpha) else 0f

    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f)
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .rotate(rotation)
            .pointerInput(isTopCard) {
                if (isTopCard) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > horizontalMinSwipeDistance && offsetY.absoluteValue < verticalMinSwipeDistance -> onSwiped()
                                offsetX < -horizontalMinSwipeDistance && offsetY.absoluteValue < verticalMinSwipeDistance -> onSwiped()
                                offsetY > verticalMinSwipeDistance && offsetX.absoluteValue < horizontalMinSwipeDistance -> onSwiped()
                                offsetY < -verticalMinSwipeDistance && offsetX.absoluteValue < horizontalMinSwipeDistance -> onSwiped()
                                else -> {
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0d0d0d)),
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Card content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray
                )
            }

            // Action texts
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 42.dp, end = 32.dp)
                    .rotate(25f)
                    .alpha(rightAlpha)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.Green)
            ) {
                Text(
                    "Know",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.Green,
                    fontSize = 24.sp
                )
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 42.dp, start = 25.dp)
                    .rotate(-25f)
                    .alpha(leftAlpha)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text(
                    "Don't Know",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.Red,
                    fontSize = 24.sp
                )
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .alpha(bottomAlpha)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(
                    "Skip",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 24.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CardsScreenPreview() {
    MemoCardsTheme {
        CardsScreen(listOf(
            Card("Hello", "Bonjour"),
            Card("Goodbye", "Au revoir"),
            Card("Please", "S'il vous plaît"),
            Card("Thank you", "Merci")
        ), {})
    }
}