package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.enums.SyncStatus

@Entity(tableName = "notes")
data class NoteModel(
    @PrimaryKey val id: Long,
    val content: String,
    val isFavorite: Boolean,
    val syncStatus: SyncStatus
)
