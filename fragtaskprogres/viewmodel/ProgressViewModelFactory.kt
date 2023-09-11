package com.improver.workable.fragtaskprogres.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.improver.workable.data.repository.TaskRepository


class ProgressViewModelFactory(private val args: String, private val repository: TaskRepository, private val application: Application,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(args, repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
