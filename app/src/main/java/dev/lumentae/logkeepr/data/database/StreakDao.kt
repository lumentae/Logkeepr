package dev.lumentae.logkeepr.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.lumentae.logkeepr.data.database.entity.StreakEntity
import java.time.Instant
import java.time.ZoneOffset

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak ORDER BY timestamp DESC")
    fun getAllStreaks(): List<StreakEntity>

    @Insert
    fun insertStreak(vararg streaks: StreakEntity)

    @Query("DELETE FROM streak")
    fun clearAll()

    fun updateStreak(newEntryTimestamp: Long = System.currentTimeMillis()) {
        val streakDays = getAllStreaks()
            .map { Instant.ofEpochMilli(it.timestamp).atZone(ZoneOffset.UTC).toLocalDate() }
            .sorted()

        val newEntryDay =
            Instant.ofEpochMilli(newEntryTimestamp).atZone(ZoneOffset.UTC).toLocalDate()
        if (streakDays.isEmpty()) {
            insertStreak(
                StreakEntity(
                    timestamp = newEntryDay.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                )
            )
            return
        }

        if (streakDays.contains(newEntryDay)) return

        val lastDay = streakDays.last()
        if (newEntryDay == lastDay.plusDays(1) || newEntryDay.isBefore(lastDay)) {
            insertStreak(
                StreakEntity(
                    timestamp = newEntryDay.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                )
            )
            return
        }

        clearAll()
        insertStreak(
            StreakEntity(
                timestamp = newEntryDay.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            )
        )
    }

    fun checkStreak(newEntryTimestamp: Long = System.currentTimeMillis()) {
        val streakDays = getAllStreaks()
            .map { Instant.ofEpochMilli(it.timestamp).atZone(ZoneOffset.UTC).toLocalDate() }
            .sorted()

        val newEntryDay =
            Instant.ofEpochMilli(newEntryTimestamp).atZone(ZoneOffset.UTC).toLocalDate()
        if (streakDays.contains(newEntryDay)) return

        val lastDay = streakDays.last()
        if (newEntryDay == lastDay.plusDays(1) || newEntryDay.isBefore(lastDay)) {
            return
        }

        clearAll()
        insertStreak(
            StreakEntity(
                timestamp = newEntryDay.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            )
        )
    }
}