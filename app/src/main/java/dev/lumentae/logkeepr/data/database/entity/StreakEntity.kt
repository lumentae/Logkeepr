package dev.lumentae.logkeepr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val timestamp: Long,
)
