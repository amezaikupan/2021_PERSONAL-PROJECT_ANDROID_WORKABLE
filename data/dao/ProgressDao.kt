package com.improver.workable.data.dao

import androidx.room.*
import com.improver.workable.data.entity.Progress
import com.improver.workable.data.relationship.ProgressWithPhoto

@Dao
interface ProgressDao {

    @Transaction
    @Query("SELECT * FROM Progress WHERE progress_id=:progressID")
    fun getProgressWithPhoto(progressID: Long): ProgressWithPhoto

    @Query("DELETE FROM Progress WHERE this_task_id =:taskID ")
    fun deleteTaskProgress(taskID: Long)


    @Query("DELETE FROM Progress WHERE progress_id =:progressID ")
    fun deleteProgress(progressID: Long)

    @Insert
    fun insertProgress(progress: Progress)

    @Query("SELECT * FROM Progress")
    fun getAllProgress(): List<Progress>

    @Query("UPDATE Progress SET this_task_id=:thisTaskID, progress_note =:progressNote, progress_time_ticker=:progressTicker WHERE progress_id=:progressID")
    fun updateProgress(thisTaskID: Long, progressNote: String, progressTicker: String, progressID: Long)

    @Query("SELECT * FROM progress WHERE this_task_id=:taskID")
    fun getThisTaskProgress(taskID: Long): List<Progress>

    @Query("SELECT progress_id FROM progress ORDER BY progress_id DESC LIMIT 1")
    fun getRecentMadeProgressID(): Long
}