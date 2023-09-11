package com.improver.workable.data.dao

import androidx.room.*
import com.improver.workable.data.entity.Task
import com.improver.workable.data.relationship.TaskWithChips
import com.improver.workable.data.relationship.TaskWithProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


    @Query("DELETE FROM Task WHERE task_id =:taskID ")
    fun deleteTask(taskID: Long)

    @Transaction
    @Query("SELECT * FROM Task")
    fun getTasksWithChips(): Flow<List<TaskWithChips>>

    @Transaction
    @Query("SELECT * FROM Task WHERE task_id=:taskid")
    fun getTaskWithChips(taskid: Long): TaskWithChips

    @Transaction
    @Query("SELECT * FROM Task WHERE task_id=:taskid")
    fun getTaskWithProgress(taskid: Long): TaskWithProgress

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(task:Task): Long


    @Query("UPDATE Task SET task_state = 0  WHERE task_id =:id")
    fun updateState(id: Long)

    @Query("SELECT task_id FROM Task ORDER BY task_id DESC LIMIT 1")
    fun getRecentTaskID(): Long?

    @Query("SELECT * FROM Task ORDER BY task_id DESC LIMIT 1")
    fun getRecentTask(): Task

    @Query("SELECT * FROM Task")
    fun getAllLiveTask(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE task_state = 1")
    fun getAllOnLiveTask(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE task_id =:taskID")
    fun getTaskById(taskID: Long): Task

}