package com.improver.workable.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.improver.workable.data.entity.Chips
import com.improver.workable.data.entity.Task

data class TaskWithChips (
    @Embedded val task: Task,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "this_chips_task_id"
    )
    val chips: List<Chips>
)