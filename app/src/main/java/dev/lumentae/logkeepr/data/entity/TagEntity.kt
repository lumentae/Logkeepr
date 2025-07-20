package dev.lumentae.logkeepr.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey var id: Long,
    @ColumnInfo(name = "project_id") var projectId: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "color") var color: String? = null
)
