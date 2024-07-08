package com.pandacorp.memocards

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pandacorp.memocards.database.CardStatus
import com.pandacorp.memocards.ui.theme.MemoCardsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(onSaveCard: (front: String, back: String, details: String, status: CardStatus) -> Unit, onCancel: () -> Unit) {
    var frontText by remember { mutableStateOf("") }
    var backText by remember { mutableStateOf("") }
    var detailsText by remember { mutableStateOf("") }

    val buttonColor = Color(0xFF0077BE)
    // Needed to change the cursor's color
    val customTextSelectionColors = TextSelectionColors(
        handleColor = buttonColor,
        backgroundColor = buttonColor.copy(alpha = 0.4f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0f1418))
    ) {
        // Top bar with back button and flags
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

@Preview(showBackground = true)
@Composable
fun AddCardScreenPreview() {
    MemoCardsTheme {
        AddCardScreen({ _, _, _, _ -> }, {})
    }
}