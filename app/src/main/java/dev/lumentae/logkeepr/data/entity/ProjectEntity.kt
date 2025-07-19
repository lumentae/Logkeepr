package dev.lumentae.logkeepr.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project")
data class ProjectEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "created_at") var createdAt: Long,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "color") var color: String? = null, // Hex color code
)
