package com.example.myapplication

import androidx.lifecycle.ViewModel
import com.example.ktor_client.ApiClient
import com.example.models.database.Note
import com.example.models.dto.CreateNoteBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val client = ApiClient()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    init {
        loadNotes()
    }

    private fun loadNotes() {
        scope.launch {
            val notes = client.getNotes()
            _notes.value = notes.map { Note(it.id, it.content) }
        }
    }

    fun addNote(note: String) {
        scope.launch {
            client.addNote(CreateNoteBody(note))
            loadNotes()
        }
    }

    fun deleteNote(note: Note) {
        scope.launch {
            client.deleteNote(note.id)
            loadNotes()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}