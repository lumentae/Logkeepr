package dev.lumentae.logkeepr.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.lumentae.logkeepr.data.entity.*

@Database(
    entities = [EntryEntity::class, ProjectEntity::class, TagEntity::class, StreakEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun streakDao(): StreakDao
}