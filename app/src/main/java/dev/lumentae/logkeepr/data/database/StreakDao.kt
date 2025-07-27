package dev.lumentae.logkeepr.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.lumentae.logkeepr.data.database.entity.StreakEntity

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak ORDER BY timestamp DESC")
    fun getAllStreaks(): List<StreakEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStreak(vararg streaks: StreakEntity)

    @Query("DELETE FROM streak")
    fun clearAll()
}