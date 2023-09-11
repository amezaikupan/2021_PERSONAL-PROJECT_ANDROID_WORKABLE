package com.improver.workable.fraguserdata.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.improver.workable.data.repository.TaskRepository


class UserDataViewModelFactory(private val repository: TaskRepository, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDataViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
