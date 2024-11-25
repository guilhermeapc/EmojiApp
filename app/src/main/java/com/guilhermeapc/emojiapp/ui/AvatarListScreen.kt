// ui/AvatarListScreen.kt
package com.guilhermeapc.emojiapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.viewmodel.EmojiViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarListScreen(navController: NavController, viewModel: EmojiViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Avatar List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Navigate back
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.testTag("progress_indicator"),
                            )
                        }
                    }

                    uiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    uiState.cachedUsers.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(
                                items = uiState.cachedUsers,
                                key = { it.login } // Unique key based on GitHub username
                            ) { user ->
                                AvatarListItem(user = user) { selectedUser ->
                                    // Handle avatar deletion
                                    viewModel.deleteGitHubUser(selectedUser)
                                    coroutineScope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Deleted ${selectedUser.login}",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.addGitHubUser(selectedUser)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Placeholder prompt
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No avatars cached. Search for a user to add.")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AvatarListItem(user: GitHubUser, onDelete: (GitHubUser) -> Unit) {
    Card(
        onClick = { onDelete(user) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar
            Image(
                painter = rememberAsyncImagePainter(user.avatar_url),
                contentDescription = user.login,
                modifier = Modifier
                    .size(64.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Username
            Text(
                text = user.login,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

