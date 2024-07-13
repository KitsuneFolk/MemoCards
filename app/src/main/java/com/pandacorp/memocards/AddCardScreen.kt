package com.pandacorp.memocards

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pandacorp.memocards.database.CardItem
import com.pandacorp.memocards.database.CardStatus
import com.pandacorp.memocards.ui.theme.MemoCardsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    onSaveCard: (front: String, back: String, details: String, status: CardStatus) -> Unit,
    onCancel: () -> Unit
) {
    var frontText by remember { mutableStateOf("") }
    var backText by remember { mutableStateOf("") }
    var detailsText by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var importedCards by remember { mutableStateOf(listOf<CardItem>()) }

    val context = LocalContext.current
    val buttonColor = Color(0xFF0077BE)
    val customTextSelectionColors = TextSelectionColors(
        handleColor = buttonColor,
        backgroundColor = buttonColor.copy(alpha = 0.4f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0f1418))
    ) {
        // Top bar with back button and import button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                val clipboardContent = getClipboardContent(context)
                importedCards = parseClipboardContent(clipboardContent)
                showImportDialog = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_upload),
                    contentDescription = "Import",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (showImportDialog) {
            ImportDialog(
                cards = importedCards,
                onDismiss = { showImportDialog = false },
                onImport = { cards ->
                    cards.forEach { card ->
                        onSaveCard(card.front, card.back, card.details, CardStatus.TO_LEARN)
                    }
                    showImportDialog = false
                }
            )
        }

        // Camera icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.add_image_icon),
                contentDescription = "Add Photo",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        }

        // Custom TextField function to avoid repetition
        @Composable
        fun CustomTextField(
            value: String,
            onValueChange: (String) -> Unit,
            label: String
        ) {
            var isFocused by remember { mutableStateOf(false) }

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = {
                        Text(
                            label,
                            color = if (isFocused) Color.Cyan else Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .onFocusChanged { isFocused = it.isFocused }
                        .background(Color(0xFF0d0d0d), shape = RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        containerColor = Color.Transparent,
                        cursorColor = buttonColor,
                        focusedIndicatorColor = Color.Cyan,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )
            }
        }

        // Front input
        CustomTextField(
            value = frontText,
            onValueChange = { frontText = it },
            label = "Front"
        )

        // Back input
        CustomTextField(
            value = backText,
            onValueChange = { backText = it },
            label = "Back"
        )

        // Details input
        CustomTextField(
            value = detailsText,
            onValueChange = { detailsText = it },
            label = "Details"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Save button
        Button(
            onClick = { onSaveCard(frontText, backText, detailsText, CardStatus.TO_LEARN) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Text("SAVE", color = Color.White)
        }
    }
}


@Composable
fun ImportDialog(
    cards: List<CardItem>,
    onDismiss: () -> Unit,
    onImport: (List<CardItem>) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0f1418),
            modifier = Modifier.fillMaxHeight(0.7f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Import Cards",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Total cards to import: ${cards.size}",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (cards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No importable content found in clipboard.\n" +
                                    "Format should be:\n" +
                                    "front　back　details\n" +
                                    "(separated by full-width spaces)",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(cards) { index, card ->
                            CardPreviewItem(card, index + 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onImport(cards) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.85f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077BE))
                ) {
                    Text("Import", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun CardPreviewItem(card: CardItem, number: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$number.",
            color = Color.White,
            modifier = Modifier.width(30.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a1a))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(card.front, color = Color.White)
                Text(card.back, color = Color.Gray)
                if (card.details.isNotEmpty()) {
                    Text(card.details, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

fun getClipboardContent(context: Context): String {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
}

fun parseClipboardContent(content: String): List<CardItem> {
    return content.split("\n").mapNotNull { line ->
        val parts = line.split("　")
        if (parts.size >= 2) {
            CardItem(
                front = parts[0],
                back = parts[1],
                details = parts.getOrNull(2) ?: "",
                status = CardStatus.TO_LEARN
            )
        } else {
            null
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCardScreenPreview() {
    MemoCardsTheme {
        AddCardScreen({ _, _, _, _ -> }, {})
    }
}