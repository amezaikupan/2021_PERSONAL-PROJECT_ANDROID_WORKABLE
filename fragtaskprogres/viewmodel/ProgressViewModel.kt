package com.improver.workable.fragtaskprogres.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.improver.workable.Utils.notification.TaskReceiver
import com.improver.workable.data.entity.Chips
import com.improver.workable.data.entity.Task
import com.improver.workable.data.relationship.ProgressWithPhoto
import com.improver.workable.data.relationship.TaskWithProgress
import com.improver.workable.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ProgressViewModel(
    private val args: String,
    private val repository: TaskRepository,
    application: Application,
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private val thisApplication = application
    private val alarmManager = thisApplication.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(application, TaskReceiver::class.java)


//    suspend fun getThisTaskProgress(): TaskWithProgress {
//        return repository.getTaskProgressByID(args.toLong())
//    }

    suspend fun getThisTaskProgress(): TaskWithProgress{
        return repository.getTaskProgressByID(args.toLong())
    }

    suspend fun getThisTask(): Task {
        return repository.getTaskByID(args.toLong())
    }


    suspend fun getThisProgressPhoto(id:Long): ProgressWithPhoto{
        return repository.getProgressPhotoByID(id)
    }

    suspend fun getThisTaskChips(): List<Chips>{
        return repository.getAllTaskChips(args.toLong())

    }

    fun updateState() = viewModelScope.launch {
        repository.UpdateState(args.toLong())
    }

    fun cancelNotification(){

        PendingIntent.getBroadcast(getApplication(), args.toInt(),notifyIntent,0).cancel()
    }

}