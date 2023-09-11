package com.improver.workable.fragactmain.viewmodel

import android.app.Application
import android.app.NotificationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.improver.workable.data.entity.Task
import com.improver.workable.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ActMainViewModel(
    private val repository: TaskRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    //get task data
    var allOnLiveTask : LiveData<List<Task>> = repository.allOnLiveTask.asLiveData()

    //update task state if deadline has passed
    fun updateState(id: Long) = viewModelScope.launch {
        repository.UpdateState(id)
    }
    //-----------------------------------------

    val notificationManager = ContextCompat.getSystemService(
        application,
        NotificationManager::class.java
    ) as NotificationManager







}
