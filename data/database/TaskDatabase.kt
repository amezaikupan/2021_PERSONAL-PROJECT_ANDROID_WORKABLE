package com.improver.workable.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.improver.workable.data.dao.ChipDao
import com.improver.workable.data.dao.PhototDao
import com.improver.workable.data.dao.ProgressDao
import com.improver.workable.data.dao.TaskDao
import com.improver.workable.data.entity.*


@Database(entities = [Task::class, Progress::class, Chips::class, Photo::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun progressDao(): ProgressDao
    abstract fun chipDao(): ChipDao
    abstract fun photoDao(): PhototDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getProjectDatabase(context: Context): TaskDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "task_database",
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance

                }
                return instance
            }
        }
    }
}