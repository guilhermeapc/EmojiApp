// MainScreen.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.guilhermeapc.emojiapp.viewmodel.EmojiViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MainScreen(navController: NavController, viewModel: EmojiViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    var username by remember { mutableStateOf("") }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display logic
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error
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
                            Text(
                                text = it.name, style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                else -> {
                    // Placeholder prompt
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                        text = "Visit your lists, \nget a Random Emoji or \nsearch for a GitHub user to add!"
                    )
                }
            }
            // Button to fetch a random emoji
            Button(
                onClick = {
                    Timber.d("Random Emoji button clicked")
                    viewModel.getRandomEmoji()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Random Emoji")
            }

            // Emoji List Butyon
            Button(
                onClick = {
                    Timber.d("Emoji List button clicked")
                    navController.navigate("emoji_list_screen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Emoji List")
            }

            // Avatar Search
            // Input Field for Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("GitHub Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        if (username.isNotBlank()) {
                            viewModel.searchGitHubUser(username)
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter a username")
                            }
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (username.isNotBlank()) {
                        viewModel.searchGitHubUser(username)
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter a username")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Displaying the Avatar
            when {
                uiState.isSearching -> {
                    CircularProgressIndicator()
                }

                uiState.searchedUser != null -> {
                    uiState.searchedUser?.let {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(it.avatar_url),
                                contentDescription = it.login,
                                modifier = Modifier
                                    .size(128.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = it.login, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                uiState.searchError != null -> {
                    Text(
                        text = "Error: ${uiState.searchError}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Avatar List Button
            Button(
                onClick = { navController.navigate("avatar_list") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Avatar List",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Avatar List")
            }
        }
    })
}
