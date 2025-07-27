package dev.lumentae.logkeepr.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity(tableName = "project")
data class ProjectEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "created_at") var createdAt: Long,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "color") var color: String? = null, // Hex color code
    @ColumnInfo(name = "time_spent") var timeSpent: Long = 0L // Time spent in seconds
)
