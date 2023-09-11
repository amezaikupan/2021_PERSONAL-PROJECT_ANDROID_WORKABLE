package com.improver.workable.fragpostrun.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.improver.workable.Utils.notification.TaskReceiver
import com.improver.workable.data.entity.Chips
import com.improver.workable.data.entity.Progress
import com.improver.workable.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class PostRunTaskViewModel(
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
    private val notifyIntent = Intent(application, TaskReceiver::class.java)

    //get chips by task id
    suspend fun getThisChipList(): List<Chips> {
        return repository.getAllTaskChips(args.toLong())
    }
    //--------------------------------

    fun insertProgress(progress: Progress) =
        viewModelScope.launch { repository.InsertProgress(progress) }

    fun insertPhoto(uri: String) = viewModelScope.launch {
        repository.InsertPhoto(uri)
    }

    fun updateChips(id: Long) = viewModelScope.launch { repository.UpdateChip(id) }


    fun updateState() = viewModelScope.launch {
        repository.UpdateState(args.toLong())
    }

    fun updateProgress(thisTaskID: Long, progressNote: String, progressTicker: String) = viewModelScope.launch {
        repository.UpdateProgress(thisTaskID, progressNote, progressTicker)
    }

    fun deleteProgress() = viewModelScope.launch {
        repository.deleteProgress()
    }
    fun cancelNotification(){

        PendingIntent.getBroadcast(getApplication(), args.toInt(),notifyIntent,0).cancel()

    }

}