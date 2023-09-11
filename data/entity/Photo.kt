package com.improver.workable.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo (
    @PrimaryKey(autoGenerate = true) val photo_id : Long = 0L,
    @ColumnInfo(name = "this_photo_progress_id") var this_photo_progress_id: Long = 0L,
    @ColumnInfo(name = "photo_uri") var photo_uri: String = ""

)