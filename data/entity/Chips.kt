package com.improver.workable.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chips (
    @PrimaryKey(autoGenerate = true) var chips_id: Long = 0L,
    @ColumnInfo(name = "this_chips_task_id") var this_chip_task_id: Long = 0L,
    @ColumnInfo(name = "chip_string")  var chips: String = "",
    @ColumnInfo(name = "chip_check_state") var chip_check_state : Boolean = false
)