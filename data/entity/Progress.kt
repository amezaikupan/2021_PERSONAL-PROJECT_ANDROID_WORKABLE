package com.improver.workable.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Progress (
    @PrimaryKey(autoGenerate = true) val progress_id: Long = 0L,

    @ColumnInfo(name = "this_task_id") var this_task_id: Long = 0L,

    @ColumnInfo(name = "progress_note") var progress_note: String = "",

    @ColumnInfo(name = "progress_time_ticker") var progress_ticker :String = "",

    @ColumnInfo(name = "progress_date") val progress_date: Long = System.currentTimeMillis()

)