// EmojiViewModel.kt
package com.guilhermeapc.emojiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.model.GitHubUser
import com.guilhermeapc.emojiapp.repository.EmojiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import timber.log.Timber

data class EmojiUiState(
    val isLoading: Boolean = false,
    val emojis: List<Emoji> = emptyList(),
    val selectedEmoji: Emoji? = null,
    val error: String? = null,
    val searchError: String? = null,
    val isRefreshing: Boolean = false,
    val searchedUser: GitHubUser? = null,
    val isSearching: Boolean = false,
    val cachedUsers: List<GitHubUser> = emptyList()
)

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val repository: EmojiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmojiUiState())
    val uiState: StateFlow<EmojiUiState> = _uiState.asStateFlow()

    init {
        // Observe cached GitHub users
        viewModelScope.launch {
            repository.getAllGitHubUsers().collect { users ->
                Timber.d("Cached users updated: $users")
                _uiState.update { it.copy(cachedUsers = users) }
            }
        }
    }

    fun fetchEmojis() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isRefreshing = true) }
            try {
                val emojis = repository.getEmojis()
                Timber.d("Received emojis: $emojis")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        emojis = emojis,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching emojis")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "An unexpected error occurred",
                        isRefreshing = false
                    )
                }
            }
        }
    }

    fun removeEmoji(emoji: Emoji) {
        viewModelScope.launch {
            val updatedList = _uiState.value.emojis.toMutableList().apply {
                remove(emoji)
            }
            _uiState.update { it.copy(emojis = updatedList) }
            Timber.d("Emoji removed: ${emoji.name}")
        }
    }

    fun addEmoji(emoji: Emoji) {
        // Used at the moment just for undoing the removal from list
        viewModelScope.launch {
            val updatedList = _uiState.value.emojis.toMutableList().apply {
                add(emoji)
            }
            _uiState.update { it.copy(emojis = updatedList) }
            Timber.d("Emoji re-added: ${emoji.name}")
        }
    }


    fun getRandomEmoji() {
        Timber.d("Getting a random emoji")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Ensure emojis are fetched
                val emojis = repository.getEmojis()
                if (emojis.isNotEmpty()) {
                    val randomEmoji = emojis.random()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            emojis = emojis,
                            selectedEmoji = randomEmoji
                        )
                    }
                    Timber.d("Random emoji selected: $randomEmoji")
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No emojis available") }
                    Timber.d("No emojis available to select")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error getting random emoji")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    // New functions for Avatar List
    fun deleteGitHubUser(user: GitHubUser) {
        viewModelScope.launch {
            repository.deleteGitHubUser(user)
            Timber.d("GitHub user deleted: ${user.login}")
        }
    }

    fun addGitHubUser(user: GitHubUser) {
        viewModelScope.launch {
            repository.addGitHubUser(user)
            Timber.d("GitHub user re-added: ${user.login}")
        }
    }

    fun searchGitHubUser(username: String) {
        viewModelScope.launch {
            if (username.isBlank()) {
                _uiState.update { it.copy(error = "Username cannot be empty") }
                return@launch
            }
            _uiState.update { it.copy(isSearching = true, error = null, searchedUser = null) }
            try {
                val user = repository.getGitHubUser(username)
                _uiState.update { it.copy(searchedUser = user, isSearching = false) }
                Timber.d("Searched user: $user")
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> {
                        if (e.code() == 404) {
                            "User not found" // Specific message for 404 error
                        } else {
                            "Failed to fetch user" // Generic message for other errors
                        }
                    }

                    else -> "Failed to fetch user" // Generic message for other exceptions
                }
                _uiState.update { it.copy(isSearching = false, searchError = errorMessage) }
            }
        }
    }
}

