package com.example.myapplication.screen.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myapplication.MainViewModel
import com.example.myapplication.R
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
                        text = stringResource(R.string.fav_label),
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
                    text = stringResource(R.string.all_notes_label),
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
            label = { Text(stringResource(R.string.note_content_label)) },
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
