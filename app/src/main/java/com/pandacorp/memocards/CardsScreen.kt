package com.pandacorp.memocards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pandacorp.memocards.database.CardItem
import com.pandacorp.memocards.ui.theme.MemoCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun CardsScreen(cardItems: List<CardItem>, onClose: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var animatingCardIndex by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            cardItems.asReversed().take(3).reversed().forEachIndexed { index, card ->
                val cardIndex = currentIndex + index
                if (cardIndex < cardItems.size) {
                    key(cardIndex) {
                        SwipeableCard(
                            cardItem = cardItems[cardIndex],
                            onSwiped = { direction ->
                                animatingCardIndex = cardIndex
                                // Use coroutineScope to call the suspend function
                                coroutineScope.launch {
                                    direction.animate {
                                        currentIndex++
                                        animatingCardIndex = null
                                    }
                                }
                            },
                            isTopCard = index == 0,
                            isAnimating = cardIndex == animatingCardIndex,
                            modifier = Modifier
                                .padding(bottom = (index * 32).dp)
                                .zIndex(100f - index.toFloat())
                        )
                    }
                }
            }
        }

        // Completion message
        if (currentIndex >= cardItems.size) {
            Text(
                "All cards completed!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

private const val HORIZONTAL_MIN_SWIPE_DISTANCE = 50
private const val VERTICAL_MIN_SWIPE_DISTANCE = 50

private const val AUTO_SWIPE_DURATION = 600
private const val FLIP_DURATION = 600

@Composable
fun SwipeableCard(
    cardItem: CardItem,
    onSwiped: (SwipeDirection) -> Unit,
    isTopCard: Boolean,
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var swipeDirection by remember { mutableStateOf<SwipeDirection?>(null) }
    var isFlipped by remember { mutableStateOf(false) }

    // Calculates the rotation based on horizontal offset for a swipe effect
    val rotation by animateFloatAsState(targetValue = offsetX * 0.1f, label = "rotation")

    // Handles the 3D flip animation of the card
    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = FLIP_DURATION),
        label = "flipRotation"
    )

    // Animates the card off-screen when swiped
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isAnimating) swipeDirection?.getTargetX() ?: 0f else 0f,
        animationSpec = tween(durationMillis = AUTO_SWIPE_DURATION),
        label = "animatedOffsetX"
    )

    // Animates the card off-screen when swiped
    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isAnimating) swipeDirection?.getTargetY() ?: 0f else 0f,
        animationSpec = tween(durationMillis = AUTO_SWIPE_DURATION),
        label = "animatedOffsetY"
    )

    // Reset offset and flip state when the card becomes the top card
    LaunchedEffect(isTopCard) {
        if (isTopCard) {
            offsetX = 0f
            offsetY = 0f
            swipeDirection = null
            isFlipped = false
        }
    }

    // Calculate alpha values for swipe indicators based on swipe direction and distance
    val horizontalAlpha = (offsetX.absoluteValue / HORIZONTAL_MIN_SWIPE_DISTANCE).coerceIn(0f, 1f)
    val verticalAlpha = (offsetY.absoluteValue / VERTICAL_MIN_SWIPE_DISTANCE).coerceIn(0f, 1f)

    val rightAlpha = if (offsetX > 0) horizontalAlpha * (1 - verticalAlpha) else 0f
    val leftAlpha = if (offsetX < 0) horizontalAlpha * (1 - verticalAlpha) else 0f
    val bottomAlpha = if (offsetY > 0) verticalAlpha * (1 - horizontalAlpha) else 0f

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f)
            .offset {
                IntOffset(
                    (offsetX + animatedOffsetX).toInt(),
                    (offsetY + animatedOffsetY).toInt()
                )
            }
            .rotate(rotation)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Apply 3D flip rotation
                    rotationY = flipRotation
                    cameraDistance = 8 * density
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0d0d0d)),
            border = BorderStroke(2.dp, Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isTopCard) {
                        if (isTopCard) {
                            detectTapGestures(
                                onTap = {
                                    if (!isFlipped) {
                                        isFlipped = true
                                    }
                                }
                            )
                        }
                    }
                    .pointerInput(isTopCard, isFlipped) {
                        if (isTopCard && isFlipped) {
                            detectDragGestures(
                                onDragEnd = {
                                    swipeDirection = when {
                                        offsetX < -HORIZONTAL_MIN_SWIPE_DISTANCE && offsetY.absoluteValue < VERTICAL_MIN_SWIPE_DISTANCE -> SwipeDirection.LEFT
                                        offsetX > HORIZONTAL_MIN_SWIPE_DISTANCE && offsetY.absoluteValue < VERTICAL_MIN_SWIPE_DISTANCE -> SwipeDirection.RIGHT
                                        offsetY > VERTICAL_MIN_SWIPE_DISTANCE && offsetX.absoluteValue < HORIZONTAL_MIN_SWIPE_DISTANCE -> SwipeDirection.DOWN
                                        else -> null
                                    }
                                    swipeDirection?.let { onSwiped(it) } ?: run {
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                if (!isAnimating) {
                                    // Invert the sign for X-axis to correct swipe direction
                                    offsetX -= dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                        }
                    }
            ) {
                // Front content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Control visibility of front content during flip
                            alpha = if (flipRotation < 90f) 1f else 0f
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cardItem.front,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }

                // Back content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Flip back content and control its visibility during flip
                            rotationY = 180f
                            alpha = if (flipRotation >= 90f) 1f else 0f
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = cardItem.front,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = cardItem.back,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = cardItem.details,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Action texts (only visible when flipped)
        if (isFlipped) {
            // "Know" indicator
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

            // "Don't Know" indicator
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

            // "Skip" indicator
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

enum class SwipeDirection {
    LEFT, RIGHT, DOWN;

    fun getTargetX() = when (this) {
        LEFT -> -2000f
        RIGHT -> 2000f
        else -> 0f
    }

    fun getTargetY() = when (this) {
        DOWN -> 2000f
        else -> 0f
    }

    suspend fun animate(onComplete: () -> Unit) {
        delay(200L)
        onComplete()
    }
}

@Preview(showBackground = true)
@Composable
fun CardsScreenPreview() {
    MemoCardsTheme {
        CardsScreen(listOf(
            CardItem(front = "Hello", back = "Bonjour"),
            CardItem(front = "Goodbye", back = "Au revoir"),
            CardItem(front = "Please", back = "S'il vous plaît"),
            CardItem(front = "Thank you", back = "Merci")
        ), {})
    }
}