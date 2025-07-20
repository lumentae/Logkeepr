package dev.lumentae.logkeepr.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.lumentae.logkeepr.data.entity.*

@Database(entities = [EntryEntity::class, ProjectEntity::class, TagEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}