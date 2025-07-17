package dev.lumentae.logkeepr.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.lumentae.logkeepr.data.entity.*

@Dao
interface ProjectDao {
    // Project query methods
    @Query("SELECT * FROM project")
    fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM project WHERE id = :projectId")
    fun getProjectById(projectId: Long): ProjectEntity?

    @Insert
    fun insertProject(vararg projects: ProjectEntity)

    @Update
    fun updateProject(project: ProjectEntity)

    @Delete
    fun deleteProject(project: ProjectEntity)


    // Taq query methods
    @Query("SELECT * FROM tags")
    fun getAllTags(): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :tagId")
    fun getTagById(tagId: Long): TagEntity?

    @Query("SELECT * FROM tags WHERE project_id = :projectId")
    fun getTagsForProject(projectId: Long): List<TagEntity>

    @Insert
    fun insertTag(vararg tags: TagEntity)

    @Update
    fun updateTag(tag: TagEntity)

    @Delete
    fun deleteTag(tag: TagEntity)


    // Entry query methods
    @Query("SELECT * FROM entries")
    fun getAllEntries(): List<EntryEntity>

    @Query("SELECT * FROM entries WHERE id = :entryId")
    fun getEntryById(entryId: Long): EntryEntity?

    @Query("SELECT * FROM entries WHERE project_id = :projectId")
    fun getEntriesForProject(projectId: Long): List<EntryEntity>

    @Insert
    fun insertEntry(vararg entries: EntryEntity)

    @Update
    fun updateEntry(entry: EntryEntity)

    @Delete
    fun deleteEntry(entry: EntryEntity)
}