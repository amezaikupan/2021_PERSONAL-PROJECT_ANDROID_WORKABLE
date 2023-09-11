package com.improver.workable.fragactmain.adapter

data class TaskObject(
    val taskName: String,
    val taskTo: String,
    val taskDeadline: Long,
    val taskId: Long,
    var TaskState: Boolean,
    var taskProgressState: Boolean = false
)