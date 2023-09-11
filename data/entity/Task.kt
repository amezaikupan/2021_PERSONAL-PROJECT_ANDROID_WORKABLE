package com.improver.workable.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val task_id: Long = 0L,
    @ColumnInfo(name = "creator_id") val creator_id: Long = 0L,
    @ColumnInfo(name = "task_name") var task_name: String = "",
    @ColumnInfo(name = "task_to") var task_to: String = "",
    @ColumnInfo(name = "task_deadline") var task_deadline: Long= 0L,
    @ColumnInfo(name = "task_created_date") val task_created_date: Long = 0L,
    @ColumnInfo(name = "task_state") var task_state : Boolean = true

)