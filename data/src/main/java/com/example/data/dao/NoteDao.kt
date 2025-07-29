package com.example.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.data.models.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotesFlow(): Flow<List<NoteModel>>

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<NoteModel>

    @Upsert()
    suspend fun upsertAll(notes: List<NoteModel>)

    @Query("UPDATE notes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
