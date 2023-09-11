package com.improver.workable.fraguserdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.improver.workable.data.entity.Photo
import com.improver.workable.data.entity.Task
import com.improver.workable.data.relationship.TaskWithProgress
import com.improver.workable.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserDataViewModel(
    private val repository: TaskRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    var allTask: LiveData<List<Task>> = repository.allTask.asLiveData()

    fun deleteTask(id: Long) = viewModelScope.launch {
        repository.DeleteTask(id)
    }
    fun deleteTaskProgress(id: Long) = viewModelScope.launch {
        repository.DeleteTaskProgress(id)

    }
    fun deleteTaskChips(id: Long) = viewModelScope.launch {
        repository.DeleteTaskChip(id)
    }

    fun deletePhoto(photo: Photo) = viewModelScope.launch {
        repository.DeletePhoto(photo)
    }

    suspend fun getThisProgressPhoto(id:Long): List<Photo> {
        return repository.getPhotoByProgressID(id)
    }

    suspend fun getThisTaskProgress(id: Long): TaskWithProgress {
        return repository.getTaskProgressByID(id)
    }
}