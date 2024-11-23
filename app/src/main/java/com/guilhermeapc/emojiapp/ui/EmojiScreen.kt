// EmojiScreen.kt

package com.guilhermeapc.emojiapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
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
                        Timber.d("Random Emoji button clicked")
                        viewModel.getRandomEmoji()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Random Emoji")
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
                    uiState.selectedEmoji != null -> {
                        uiState.selectedEmoji?.also {
                        // Display the selected random emoji
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(it.url),
                                contentDescription = it.name,
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = it.name, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    }
                    else -> {
                        // Optionally, show a placeholder or prompt
                        Text(text = "Press 'Random Emoji' to display an emoji")
                    }
                }
            }
        }
    )
}