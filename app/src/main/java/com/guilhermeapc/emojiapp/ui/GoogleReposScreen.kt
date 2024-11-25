// ui/GoogleReposScreen.kt
package com.guilhermeapc.emojiapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.guilhermeapc.emojiapp.model.GitHubRepo
import com.guilhermeapc.emojiapp.viewmodel.MainViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleReposScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val googleRepos: LazyPagingItems<GitHubRepo> = viewModel.googleRepos.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle Load States for Showing Snackbars
    LaunchedEffect(googleRepos.loadState) {
        when {
            googleRepos.loadState.refresh is LoadState.Error -> {
                val e = googleRepos.loadState.refresh as LoadState.Error
                Timber.e(e.error, "GoogleReposScreen: Error loading initial data")
                snackbarHostState.showSnackbar("Error loading repos: ${e.error.localizedMessage}")
            }

            googleRepos.loadState.append is LoadState.Error -> {
                val e = googleRepos.loadState.append as LoadState.Error
                Timber.e(e.error, "GoogleReposScreen: Error loading more data")
                snackbarHostState.showSnackbar("Error loading more repos: ${e.error.localizedMessage}")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Google GitHub Repos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Navigate back
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(count = googleRepos.itemCount) { index ->
                    googleRepos[index]?.let {
                        Timber.d("GoogleRepo: $it")
                        RepoListItem(repo = it, context = context)
                    }
                }

                // Handle Load States for Showing Loading Indicators
                googleRepos.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            Timber.d("GoogleReposScreen: Appending data - Loading")
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                        .testTag("CircularProgressIndicator") // For testing
                                )
                            }
                        }
                        // Optionally handle other load states if needed
                    }
                }
            }
        }
    )
}

@Composable
fun RepoListItem(repo: GitHubRepo, context: android.content.Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Open repository URL in browser
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/${repo.full_name}"))
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Repository Icon or Placeholder
            Image(
                painter = rememberAsyncImagePainter(repo.owner.avatar_url) ,
                contentDescription = """image of repo: ${repo.full_name} """,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = repo.full_name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (repo.private) "Private" else "Public",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
