package dev.lumentae.logkeepr.data.database

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.room.Room
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.data.database.entity.EntryEntity
import dev.lumentae.logkeepr.data.database.entity.ProjectEntity
import dev.lumentae.logkeepr.data.database.entity.StreakEntity
import dev.lumentae.logkeepr.data.database.entity.TagEntity
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Instant
import java.time.ZoneOffset

object DatabaseManager {
    private val _projects = MutableStateFlow<List<ProjectEntity>>(emptyList())
    private val _entries = MutableStateFlow<List<EntryEntity>>(emptyList())
    private val _tags = MutableStateFlow<List<TagEntity>>(emptyList())
    private val _streaks = MutableStateFlow<List<StreakEntity>>(emptyList())

    private lateinit var projectDao: ProjectDao
    private lateinit var streakDao: StreakDao

    var databaseInitialized = false
    fun initDatabase(context: Context) {
        if (databaseInitialized) return

        databaseInitialized = true
        Globals.DATABASE = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "logkeepr"
        )
            .allowMainThreadQueries()
            .build()

        projectDao = Globals.DATABASE.projectDao()
        streakDao = Globals.DATABASE.streakDao()

        loadDatabase()
    }

    fun loadDatabase() {
        _projects.value = projectDao.getAllProjects()
        _entries.value = projectDao.getAllEntries()
        _tags.value = projectDao.getAllTags()
        _streaks.value = streakDao.getAllStreaks()

    }

    fun saveDatabase() {
        Globals.DATABASE_SCOPE.launch {
            projectDao.insertProject(*_projects.value.toTypedArray())
            projectDao.insertEntry(*_entries.value.toTypedArray())
            projectDao.insertTag(*_tags.value.toTypedArray())
            streakDao.insertStreak(*_streaks.value.toTypedArray())
        }
    }

    fun resetData() {
        _projects.value = emptyList()
        _entries.value = emptyList()
        _tags.value = emptyList()
        _streaks.value = emptyList()

        Globals.DATABASE_SCOPE.launch {
            projectDao.clearAllProjects()
            projectDao.clearAllEntries()
            projectDao.clearAllTags()
            streakDao.clearAll()
        }
    }

    fun exportDatabase(): String {
        val projects = Json.encodeToJsonElement(_projects.value)
        val entries = Json.encodeToJsonElement(_entries.value)
        val tags = Json.encodeToJsonElement(_tags.value)
        val streaks = Json.encodeToJsonElement(_streaks.value)

        return Json.encodeToString(listOf(projects, entries, tags, streaks))
    }

    fun importDatabase(file: PlatformFile) {
        Globals.DATABASE_SCOPE.launch {
            projectDao.clearAllProjects()
            projectDao.clearAllEntries()
            projectDao.clearAllTags()
            streakDao.clearAll()

            val data = file.readString()
            val (projects, entries, tags, streaks) = Json.decodeFromString<List<JsonElement>>(data)
            Log.d("DatabaseManager", "Importing data: $data")

            _projects.value = Json.decodeFromJsonElement(projects)
            _entries.value = Json.decodeFromJsonElement(entries)
            _tags.value = Json.decodeFromJsonElement(tags)
            _streaks.value = Json.decodeFromJsonElement(streaks)
            saveDatabase()
        }
    }

    // Project query methods
    fun getAllProjects(): StateFlow<List<ProjectEntity>> {
        return _projects.asStateFlow()
    }

    fun getProjectById(projectId: Long): ProjectEntity? {
        return _projects.value.find { it.id == projectId }
    }

    fun insertProject(vararg projects: ProjectEntity) {
        _projects.value += projects.toList()
        Globals.DATABASE_SCOPE.launch {
            projectDao.insertProject(*projects)
        }
    }

    fun updateProject(project: ProjectEntity) {
        _projects.value = _projects.value.map {
            if (it.id == project.id) project else it
        }
        Globals.DATABASE_SCOPE.launch {
            projectDao.updateProject(project)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        _projects.value = _projects.value.filter { it.id != project.id }
        Globals.DATABASE_SCOPE.launch {
            projectDao.deleteProject(project)
        }
    }

    fun getProjectTimeSpent(projectId: Long): Long {
        return _projects.value.find { it.id == projectId }?.timeSpent ?: 0
    }

    fun getLastChangedProject(): ProjectEntity? {
        val lastChanged = _entries.value.maxByOrNull { it.timestamp }
        return lastChanged?.let { entry ->
            _projects.value.find { it.id == entry.projectId }
        }
    }

    // Taq query methods
    fun getTagsForProject(projectId: Long): List<TagEntity> {
        return _tags.value.filter { it.projectId == projectId }
    }

    fun insertTag(vararg tags: TagEntity) {
        _tags.value += tags.toList()
        Globals.DATABASE_SCOPE.launch {
            projectDao.insertTag(*tags)
        }
    }

    fun updateTag(tag: TagEntity) {
        _tags.value = _tags.value.map {
            if (it.id == tag.id) tag else it
        }
        Globals.DATABASE_SCOPE.launch {
            projectDao.updateTag(tag)
        }
    }

    fun deleteTag(tag: TagEntity) {
        _tags.value = _tags.value.filter { it.id != tag.id }
        Globals.DATABASE_SCOPE.launch {
            projectDao.deleteTag(tag)
        }
    }

    // Entry query methods
    fun getAllEntries(): StateFlow<List<EntryEntity>> {
        return _entries.asStateFlow()
    }

    fun getEntryById(entryId: Long): EntryEntity? {
        return _entries.value.find { it.id == entryId }
    }

    fun getEntriesForProject(projectId: Long): List<EntryEntity> {
        return _entries.value.filter { it.projectId == projectId }
    }

    fun insertEntry(vararg entries: EntryEntity) {
        _entries.value += entries.toList()
        Globals.DATABASE_SCOPE.launch {
            projectDao.insertEntry(*entries)
        }
    }

    fun updateEntry(entry: EntryEntity) {
        _entries.value = _entries.value.map {
            if (it.id == entry.id) entry else it
        }
        Globals.DATABASE_SCOPE.launch {
            projectDao.updateEntry(entry)
        }
    }

    fun deleteEntry(entry: EntryEntity) {
        _entries.value = _entries.value.filter { it.id != entry.id }
        Globals.DATABASE_SCOPE.launch {
            projectDao.deleteEntry(entry)
        }
    }

    // Streak query methods
    fun getAllStreaks(): StateFlow<List<StreakEntity>> {
        return _streaks.asStateFlow()
    }

    fun insertStreak(vararg streaks: StreakEntity) {
        _streaks.value += streaks.toList()
        Globals.DATABASE_SCOPE.launch {
            streakDao.insertStreak(*streaks)
        }
    }

    fun clearAll() {
        _streaks.value = emptyList()
        Globals.DATABASE_SCOPE.launch {
            streakDao.clearAll()
        }
    }

    fun updateStreak(newEntryTimestamp: Long = System.currentTimeMillis()) {
        val streakDays = getAllStreaks().value
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
        val streakDays = getAllStreaks().value
            .map { Instant.ofEpochMilli(it.timestamp).atZone(ZoneOffset.UTC).toLocalDate() }
            .sorted()

        val newEntryDay =
            Instant.ofEpochMilli(newEntryTimestamp).atZone(ZoneOffset.UTC).toLocalDate()
        if (streakDays.contains(newEntryDay) || streakDays.isEmpty()) return

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

    fun setStreak(length: MutableState<Int>) {
        clearAll()
        var timestamp = System.currentTimeMillis()
        for (i in 0 until length.value) {
            timestamp -= 24 * 60 * 60 * 1000L // Subtract one day in milliseconds
            insertStreak(
                StreakEntity(
                    timestamp = Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneOffset.UTC).toLocalDate()
                        .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                )
            )
        }
    }
}