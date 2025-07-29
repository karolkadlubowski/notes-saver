package com.example.myapplication.screen.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.data.models.NoteModel

@Composable
fun DismissableNote(modifier: Modifier = Modifier, note: NoteModel, onDismiss: () -> Unit,
                    onToggleFavorite: () -> Unit = {}) {
    val density = LocalDensity.current
    val confirmValueChange = { it: SwipeToDismissBoxValue -> it == SwipeToDismissBoxValue.StartToEnd }
    val positionalThreshold = { it: Float -> it / 3 * 2}
    val dismissState = remember {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            density = density,
            confirmValueChange = confirmValueChange,
            positionalThreshold = positionalThreshold
        )
    }
    val backgroundColor by
        rememberUpdatedState(
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> lerp(Color.Transparent, Color.Red, dismissState.progress)
                else -> Color.Transparent
            }
        )
    if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
        LaunchedEffect(note.id) {
            onDismiss()
        }
    }

    SwipeToDismissBox(
        modifier = modifier.fillMaxWidth(),
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(backgroundColor),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.content,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { onToggleFavorite() }) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (note.isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }    }
}
