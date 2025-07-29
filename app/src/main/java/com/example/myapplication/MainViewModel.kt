package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.NoteModel
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<NoteModel>> = repository.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.Default)  {
            repository.syncNotes()
        }
    }

    fun addNote(content: String) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.addNote(content)
        }
    }

    fun toggleFavorite(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default)  {
            repository.toggleFavorite(
                NoteModel(note.id, note.content, note.isFavorite)
            )
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default)  {
            repository.deleteNote(note)
        }
    }
}
