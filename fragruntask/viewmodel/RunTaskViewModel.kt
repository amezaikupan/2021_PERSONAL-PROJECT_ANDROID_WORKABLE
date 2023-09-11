package com.improver.workable.fragruntask.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import com.improver.workable.R
import com.improver.workable.data.entity.Chips
import com.improver.workable.data.entity.Task
import com.improver.workable.data.repository.TaskRepository
import kotlinx.coroutines.Job

class RunTaskViewModel(
    private val args: String,
    private val repository: TaskRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //get task by name
    suspend fun getTaskByNAme(): Task {
        return repository.getTaskByID(args.toLong())
    }
    //------------------------------

    //get chips by task id
    suspend fun getThisChipList(): List<Chips>{
        return repository.getAllTaskChips(args.toLong())
    }
    //--------------------------------

    companion object{
        private const val ONE_SECOND = 1000L

        private const val ONE_MINUTE = 60000L    }

    //Set timer that survive configuration
    lateinit var timer: CountDownTimer


    fun timerString(textView: TextView, timeItemSet: Int){

        timer = object : CountDownTimer(timeItemSet * ONE_MINUTE, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {

                val timeResult =
                    "${(millisUntilFinished / ONE_MINUTE).toString().padStart(2, '0')}:" +
                            "${(millisUntilFinished / 1000 % 60).toString().padStart(2, '0')} "
                textView.text = timeResult
            }
            override fun onFinish() {
                textView.text = "Finished!"
                timerStop()

                var mediaPlayer: MediaPlayer = MediaPlayer.create(textView.context, R.raw.finish_counter_notif)
                mediaPlayer.start()
            }
        }

        timer.start()
    }

    fun timerStop(){
        timer.cancel()
    }
    //--------------------------------------------

    //Check up on checked chip time
    fun taskTimeSwitch(time: String): Int {

        var timeLong: Int = 0
        when (time) {
            "2 Mins" -> timeLong = 2
            "5 Mins" -> timeLong = 5
            "15 Mins" -> timeLong = 15
            "20 Mins" -> timeLong = 20
            "30 Mins" -> timeLong = 30
            "45 Mins" -> timeLong = 45
            "60 Mins" -> timeLong = 60
            "120 Mins" -> timeLong = 120
        }
        return timeLong
    }
    //--------------------------------------------

}