package com.improver.workable.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.improver.workable.data.entity.Progress
import com.improver.workable.data.entity.Task

data class TaskWithProgress (
    @Embedded val task: Task,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "this_task_id"
    )
    val progress: List<Progress>
)