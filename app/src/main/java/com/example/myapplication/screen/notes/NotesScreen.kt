package com.example.myapplication.screen.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myapplication.MainViewModel
import com.example.myapplication.screen.notes.components.DismissableNote
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotesScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val layoutDirection = LocalLayoutDirection.current

    val gestureInsets = WindowInsets.safeGestures.asPaddingValues()
    val statusBarInset = WindowInsets.statusBars.asPaddingValues()
    val imeInset = WindowInsets.ime.asPaddingValues()
    val navigationInset = WindowInsets.navigationBars.asPaddingValues()

    val allNotes = notes
    val favoriteNotes = notes.filter { it.isFavorite }

    Column(modifier = Modifier.fillMaxSize()) {
        var input by remember { mutableStateOf("") }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                top = statusBarInset.calculateTopPadding(),
                start = gestureInsets.calculateStartPadding(layoutDirection),
                end = gestureInsets.calculateEndPadding(layoutDirection),
                bottom = 8.dp
            ),
        ) {
            if (favoriteNotes.isNotEmpty()) {
                item {
                    Text(
                        text = "Ulubione",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                    )
                }

                items(favoriteNotes, key = { "fav" + it.id }) { note ->
                    DismissableNote(
                        note = note,
                        onDismiss = { viewModel.deleteNote(note) },
                        onToggleFavorite = { viewModel.toggleFavorite(note) }
                    )
                }
            }

            item {
                Text(
                    text = "Wszystkie notatki",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )
            }

            items(allNotes, key = { it.id }) { note ->
                DismissableNote(
                    note = note,
                    onDismiss = { viewModel.deleteNote(note) },
                    onToggleFavorite = { viewModel.toggleFavorite(note) }
                )
            }
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingValues(
                        start = gestureInsets.calculateStartPadding(layoutDirection),
                        end = gestureInsets.calculateEndPadding(layoutDirection),
                        bottom = imeInset.calculateBottomPadding() +
                                navigationInset.calculateBottomPadding(),
                    )
                ),
            value = input,
            onValueChange = { input = it },
            label = { Text("Treść notatki") },
            keyboardActions = KeyboardActions(
                onSend = {
                    if (input.isNotBlank()) {
                        viewModel.addNote(input)
                        input = ""
                    }
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        )
    }
}
