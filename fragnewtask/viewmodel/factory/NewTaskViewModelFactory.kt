package com.improver.workable.fragnewtask.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.fragnewtask.viewmodel.NewTaskViewModel


class NewTaskViewModelFactory(
    private val repository: TaskRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewTaskViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
