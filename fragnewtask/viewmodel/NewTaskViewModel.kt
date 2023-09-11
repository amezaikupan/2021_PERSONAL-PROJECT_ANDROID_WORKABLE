package com.improver.workable.fragnewtask.viewmodel

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.*
import com.improver.workable.Utils.notification.TaskReceiver
import com.improver.workable.data.entity.Task
import com.improver.workable.data.repository.TaskRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

class NewTaskViewModel(
    private val repository: TaskRepository,
    application: Application
) : AndroidViewModel(application) {


    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(application, TaskReceiver::class.java)
    private val disposable = CompositeDisposable()



    fun insertTask(task: Task){
        disposable.add(
            repository.InsertTask(task)
                ?.subscribeOn(Schedulers.newThread())
                ?.subscribeWith(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(newReturnId: Long?) {
                        setNoifDeadline(task.task_deadline, newReturnId, task.task_name )
                    }
                    override fun onError(e: Throwable?) {
                    }

    }))
    }


    fun insertChip(chip: String) = viewModelScope.launch {
        repository.InsertChips(chip)

    }

    private fun setNoifDeadline(deadline: Long, notifID: Long?, notifTittle: String){

//        SystemClock.elapsedRealtime()
        val oneHour = 3600000
        val oneMinutes = 60000
        val notifId = notifID?.toInt()
        //to delete
        var triggerTime = deadline + 9*oneHour + 20*oneMinutes
        var thisTime =  SystemClock.elapsedRealtime()

        notifyIntent.putExtra("NotifID", notifId)
        notifyIntent.putExtra("NotifName", notifTittle)

        val alarmIntent =
            notifId?.let { PendingIntent.getBroadcast(getApplication(), it, notifyIntent, 0) }

        if (alarmIntent != null) {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                alarmIntent
            )
        }

    }


}