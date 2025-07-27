package dev.lumentae.logkeepr.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey var id: Long,
    @ColumnInfo(name = "project_id") var projectId: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "color") var color: String? = null
)
