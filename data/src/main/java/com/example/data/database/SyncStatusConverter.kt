package com.example.data.database

import androidx.room.TypeConverter
import com.example.models.enums.SyncStatus

class SyncStatusConverter {

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}
