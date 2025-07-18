package dev.lumentae.logkeepr.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "project_id") val projectId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String? = null,
    @ColumnInfo(name = "icon") val icon: String? = null,
)
