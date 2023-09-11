package com.improver.workable.data.dao

import androidx.room.*
import com.improver.workable.data.entity.Photo

@Dao
interface PhototDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photo: Photo)

    @Delete
    fun deletePhoto(photo: Photo)

    @Query("SELECT * FROM Photo WHERE this_photo_progress_id =:progress_id")
    fun getProgressPhoto(progress_id: Long): List<Photo>

    @Query("SELECT * FROM Photo")
    fun getAllPhoto(): List<Photo>
}