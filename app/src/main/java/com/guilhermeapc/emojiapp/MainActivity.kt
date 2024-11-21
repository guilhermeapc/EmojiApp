package com.guilhermeapc.emojiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.guilhermeapc.emojiapp.ui.EmojiScreen
import com.guilhermeapc.emojiapp.ui.theme.EmojiappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmojiappTheme {
                EmojiScreen()
            }
        }
    }
}