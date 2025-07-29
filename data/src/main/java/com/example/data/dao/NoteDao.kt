package com.example.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.data.models.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE syncStatus != 'DELETED'")
    fun getAllNotesFlow(): Flow<List<NoteModel>>

    @Query("SELECT * FROM notes WHERE syncStatus != 'DELETED'")
    fun getAllNotes(): List<NoteModel>

    @Query("SELECT * FROM notes WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingNotes(): List<NoteModel>

    @Upsert
    suspend fun upsertAll(notes: List<NoteModel>)

    @Upsert
    suspend fun upsert(note: NoteModel)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
