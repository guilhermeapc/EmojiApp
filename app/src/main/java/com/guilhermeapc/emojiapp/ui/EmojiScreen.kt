// EmojiScreen.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.viewmodel.EmojiViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber

@Composable
fun EmojiScreen(viewModel: EmojiViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        Timber.d("Get Emoji button clicked")
                        viewModel.fetchEmojis()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Get Emoji")
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }
                    uiState.error != null -> {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        EmojiList(emojis = uiState.emojis)
                    }
                }
            }
        }
    )
}

@Composable
fun EmojiList(emojis: List<Emoji>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(emojis) { emoji ->
            EmojiItem(emoji = emoji)
            HorizontalDivider()
        }
    }
}

@Composable
fun EmojiItem(emoji: Emoji) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Using Coil to load the emoji image
        Image(
            painter = rememberAsyncImagePainter(emoji.url),
            contentDescription = emoji.name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = emoji.name, style = MaterialTheme.typography.bodyMedium)
    }
}
