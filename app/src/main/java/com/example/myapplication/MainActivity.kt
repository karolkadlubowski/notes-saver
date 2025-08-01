package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.myapplication.screen.notes.NotesScreen
import com.example.myapplication.sync.SyncService
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        startService(Intent(this, SyncService::class.java))

        setContent {
            MyApplicationTheme {
                NotesScreen()
            }
        }
    }
}
