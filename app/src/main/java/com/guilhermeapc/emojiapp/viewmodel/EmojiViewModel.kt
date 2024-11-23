// EmojiViewModel.kt
package com.guilhermeapc.emojiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermeapc.emojiapp.model.Emoji
import com.guilhermeapc.emojiapp.repository.EmojiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber

data class EmojiUiState(
    val isLoading: Boolean = false,
    val emojis: List<Emoji> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val repository: EmojiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmojiUiState())
    val uiState: StateFlow<EmojiUiState> = _uiState.asStateFlow()

    fun fetchEmojis() {
        Timber.d("Fetching emojis from repository")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val emojis = repository.getEmojis()
                Timber.d("Received emojis: $emojis")
                _uiState.update { it.copy(isLoading = false, emojis = emojis) }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching emojis")
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "An unexpected error occurred") }
            }
        }
    }
}
