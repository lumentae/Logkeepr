package dev.lumentae.logkeepr.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val timestamp: Long,
)
