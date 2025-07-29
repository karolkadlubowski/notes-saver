package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.NoteModel
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val notes: StateFlow<List<NoteModel>> = repository.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote(content: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                repository.addNote(content)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add note", e)
            }
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                repository.deleteNote(note)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete note (id=${note.id})", e)
            }
        }
    }

    fun toggleFavorite(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                repository.toggleFavorite(note)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle favorite for note (id=${note.id})", e)
            }
        }
    }
}
