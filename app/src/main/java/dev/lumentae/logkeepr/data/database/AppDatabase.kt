package dev.lumentae.logkeepr.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.lumentae.logkeepr.data.database.entity.EntryEntity
import dev.lumentae.logkeepr.data.database.entity.ProjectEntity
import dev.lumentae.logkeepr.data.database.entity.StreakEntity
import dev.lumentae.logkeepr.data.database.entity.TagEntity

@Database(
    entities = [EntryEntity::class, ProjectEntity::class, TagEntity::class, StreakEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun streakDao(): StreakDao
}