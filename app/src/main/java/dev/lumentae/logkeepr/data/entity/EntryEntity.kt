package dev.lumentae.logkeepr.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey var id: Long,
    @ColumnInfo(name = "project_id") var projectId: Long,
    @ColumnInfo(name = "timestamp") var timestamp: Long,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "content") var content: String,
)
