package com.improver.workable.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.improver.workable.data.entity.Chips

@Dao
interface ChipDao {

    @Insert
    fun insertChip(chips: Chips)

    @Query("SELECT * FROM Chips")
    fun getAllChips(): MutableList<Chips>

    @Query("DELETE FROM Chips WHERE this_chips_task_id =:taskID ")
    fun deleteTaskChip(taskID: Long)

    @Query("SELECT * FROM Chips WHERE this_chips_task_id=:parent_id")
    fun getTaskAllLChips(parent_id: Long): List<Chips>

    @Query("UPDATE Chips SET chip_check_state = 1 WHERE chips_id =:id ")
    fun updateChipState(id: Long)

    @Query("UPDATE Chips SET chip_check_state = 0 WHERE chips_id =:id ")
    fun updateChipStateFalse(id: Long)



}