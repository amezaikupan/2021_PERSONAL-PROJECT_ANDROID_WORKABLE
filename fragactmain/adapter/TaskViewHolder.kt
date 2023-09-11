package com.improver.workable.fragactmain.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.Utils.Utils
import com.improver.workable.fragactmain.ActMainFragmentDirections
import com.improver.workable.R
import com.improver.workable.databinding.ItemTaskBinding


class TaskViewHolder(
    private val binding: ItemTaskBinding,
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ResourceAsColor")

    lateinit var deadline : String
    fun bind(task: TaskObject) {

        if (task.TaskState == false) {
            binding.deadlineMonth.setTextColor(ContextCompat.getColor(binding.taskCard.context , R.color.black))
            binding.deadlineDate.setTextColor(ContextCompat.getColor(binding.taskCard.context , R.color.black))
            binding.taskCard.setCardBackgroundColor(ContextCompat.getColor(binding.taskCard.context , R.color.offtask))
            binding.deleteButton.visibility = View.VISIBLE
        }else{
            binding.taskCard.setCardBackgroundColor(ContextCompat.getColor(binding.taskCard.context , Utils.setTaskColor(Utils.dateRangeDeadlineAlarmer(task.taskDeadline))))

        }

        deadline = Utils.makeDate(task.taskDeadline)

        binding.taskName.text = task.taskName
        binding.taskTo.text = "To: " + task.taskTo
        var dateAndMonth = deadline.split("/")
        binding.deadlineDate.text = dateAndMonth[0]
        binding.deadlineMonth.text = dateAndMonth[1]
        binding.taskId.text = task.taskId.toString()


    }

    init {
        itemView.setOnClickListener {
            var taskID = binding.taskId.text
            var action =
                ActMainFragmentDirections.actionActMainFragmentToProgressFragment(taskID.toString())
            itemView.findNavController().navigate(action)

        }

    }
    fun deleteResponse(){
        binding.taskCons.visibility = View.GONE
        binding.deleteNotice.visibility = View.VISIBLE
    }
}
