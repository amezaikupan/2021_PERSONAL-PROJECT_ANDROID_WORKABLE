package com.improver.workable.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.improver.workable.data.entity.Photo
import com.improver.workable.data.entity.Progress

data class ProgressWithPhoto (
    @Embedded val progress: Progress,
    @Relation(
        parentColumn = "progress_id",
        entityColumn = "this_photo_progress_id"
    )
    val photos: List<Photo>
)