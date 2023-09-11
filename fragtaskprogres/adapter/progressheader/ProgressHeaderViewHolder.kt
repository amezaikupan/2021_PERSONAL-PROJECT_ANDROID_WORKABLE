package com.improver.workable.fragtaskprogres.adapter.progressheader

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.Utils.Utils
import com.improver.workable.databinding.ItemProgressHeaderBinding
import com.improver.workable.fragactmain.adapter.TaskObject


class ProgressHeaderViewHolder(
    private val binding: ItemProgressHeaderBinding,

    ) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("WrongConstant")
    fun bind(taskObject: TaskObject) {
        binding.taskName.text = taskObject.taskName
        binding.toTask.text = "To: " + taskObject.taskTo
        binding.deadline.text = "DEADLINE: " + Utils.makeDate(taskObject.taskDeadline)

        binding.doTaskButton.setBackgroundColor(ContextCompat.getColor(binding.doTaskButton.context,
            Utils.setTaskColor(Utils.dateRangeDeadlineAlarmer(taskObject.taskDeadline))))

        if (taskObject.taskProgressState == true){
            binding.emptyNotify.visibility = View.VISIBLE
        }
        if ( taskObject.TaskState == false) {
            binding.finishTaskButton.visibility = View.GONE
            binding.doTaskButton.visibility = View.GONE
        }

    }
    init {


    }
    fun finishedTask(){
        binding.finishTaskButton.visibility =View.GONE
        binding.doTaskButton.visibility =View.GONE
    }




}
