package com.example.data.repository

import com.example.data.dao.NoteDao
import com.example.data.models.NoteModel
import com.example.ktor_client.ApiClient
import com.example.models.dto.CreateNoteBody
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao, private val apiClient: ApiClient) {

    val notes: Flow<List<NoteModel>> = noteDao.getAllNotesFlow()

    suspend fun syncNotes() {
        val remoteNotes = apiClient.getNotes()
        val currentNotes = noteDao.getAllNotes()
        val notesToDelete =  currentNotes.filter { it.id !in remoteNotes.map { note -> note.id } }
        notesToDelete.forEach {
            noteDao.deleteById(it.id)
        }
        noteDao.insertAll(remoteNotes.map { NoteModel(it.id, it.content) })
    }

    suspend fun addNote(content: String) {
        val success = apiClient.addNote(CreateNoteBody(content))
        if (success) syncNotes()
    }

    suspend fun deleteNote(note: NoteModel) {
        val success = apiClient.deleteNote(note.id)
        if (success) noteDao.deleteById(note.id)
    }
}
