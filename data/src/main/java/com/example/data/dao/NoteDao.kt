package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    suspend fun insertAll(notes: List<NoteModel>)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
