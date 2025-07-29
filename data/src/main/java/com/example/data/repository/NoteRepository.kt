package com.example.data.repository

import com.example.data.dao.NoteDao
import com.example.data.models.NoteModel
import com.example.ktor_client.ApiClient
import com.example.models.dto.CreateNoteBody
import com.example.models.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val apiClient: ApiClient
) {

    val notes: Flow<List<NoteModel>> = noteDao.getAllNotesFlow()

    suspend fun syncNotes() {
        syncPendingChanges()

        val remoteNotes = apiClient.getNotes()
        val currentNotes = noteDao.getAllNotes()
        val remoteIds = remoteNotes.map { it.id }

        val notesToDelete =
            currentNotes.filter { it.id !in remoteIds && it.syncStatus == SyncStatus.SYNCED }
        notesToDelete.forEach { noteDao.deleteById(it.id) }

        noteDao.upsertAll(
            remoteNotes.map {
                NoteModel(
                    id = it.id,
                    content = it.content,
                    isFavorite = it.isFavorite,
                    syncStatus = SyncStatus.SYNCED
                )
            }
        )
    }

    suspend fun syncPendingChanges() {
        val pending = noteDao.getPendingNotes()

        for (note in pending) {
            when (note.syncStatus) {
                SyncStatus.CREATED -> {
                    val newId = apiClient.addNote(CreateNoteBody(note.content, note.isFavorite))
                    if (newId != null) {
                        noteDao.deleteById(note.id)
                        noteDao.upsert(
                            note.copy(id = newId, syncStatus = SyncStatus.SYNCED)
                        )
                    }
                }

                SyncStatus.CREATED_AND_MARKED -> {
                    val newId = apiClient.addNote(CreateNoteBody(note.content, note.isFavorite))
                    if (newId != null) {
                        val updated = apiClient.updateFavorite(newId, note.isFavorite)
                        if (updated) {
                            noteDao.deleteById(note.id)
                            noteDao.upsert(
                                note.copy(id = newId, syncStatus = SyncStatus.SYNCED)
                            )
                        }
                    }
                }

                SyncStatus.MARKED -> {
                    val success = apiClient.updateFavorite(note.id, note.isFavorite)
                    if (success) {
                        noteDao.upsert(note.copy(syncStatus = SyncStatus.SYNCED))
                    }
                }

                SyncStatus.DELETED -> {
                    apiClient.deleteNote(note.id)
                    noteDao.deleteById(note.id)
                }

                SyncStatus.SYNCED -> Unit
            }
        }
    }

    suspend fun addNote(content: String) {
        val note = NoteModel(
            id = System.currentTimeMillis(),
            content = content,
            isFavorite = false,
            syncStatus = SyncStatus.CREATED
        )
        noteDao.upsert(note)
        syncPendingChanges()
    }

    suspend fun toggleFavorite(note: NoteModel) {
        val newFavorite = !note.isFavorite
        val newStatus = when (note.syncStatus) {
            SyncStatus.CREATED -> SyncStatus.CREATED_AND_MARKED
            SyncStatus.CREATED_AND_MARKED -> SyncStatus.CREATED_AND_MARKED
            SyncStatus.MARKED -> SyncStatus.MARKED
            SyncStatus.SYNCED -> SyncStatus.MARKED
            SyncStatus.DELETED -> return
            else -> return
        }

        noteDao.upsert(note.copy(isFavorite = newFavorite, syncStatus = newStatus))
        syncPendingChanges()
    }

    suspend fun deleteNote(note: NoteModel) {
        val newStatus = when (note.syncStatus) {
            SyncStatus.CREATED -> {
                noteDao.deleteById(note.id)
                return
            }

            SyncStatus.CREATED_AND_MARKED -> {
                noteDao.deleteById(note.id)
                return
            }

            SyncStatus.MARKED -> SyncStatus.DELETED
            SyncStatus.SYNCED -> SyncStatus.DELETED
            SyncStatus.DELETED -> return
        }
        val updated = note.copy(syncStatus = newStatus)
        noteDao.upsert(updated)
        syncPendingChanges()
    }
}
