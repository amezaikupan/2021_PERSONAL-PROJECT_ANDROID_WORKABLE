package com.improver.workable.fragactmain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.fragactmain.ActMainFragmentDirections
import com.improver.workable.fraguserdata.UserDataFragmentDirections
import com.improver.workable.R
import com.improver.workable.databinding.ItemTaskBinding


class TaskAdapter(private val isFilter: Boolean) :
    RecyclerView.Adapter<TaskViewHolder>() {

    private lateinit var listTask: MutableList<TaskObject>

    private lateinit var setter: DisableViewSetter
    private lateinit var deleteResponse: DeleteResponse

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        if (listTask.isEmpty()) {
            setter.disableView()
        } else{
            holder.bind(listTask[position])
        }

        deleteResponse = object : DeleteResponse{
            override fun deleteResponse() {
                holder.deleteResponse()
            }
        }

        var taskID = holder.itemView.findViewById<TextView>(R.id.task_id).text.toString()

        holder.itemView.setOnClickListener {
            if (isFilter == true) {
                var action =
                    ActMainFragmentDirections.actionActMainFragmentToProgressFragment(taskID)
                holder.itemView.findNavController().navigate(action)
            } else {
                var action =
                    UserDataFragmentDirections.actionUserDataFragmentToProgressFragment(taskID)
                holder.itemView.findNavController().navigate(action)
            }
        }

        holder.itemView.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
            setter.deleteTask(taskID.toLong(), deleteResponse)
        }

    }

    fun getItem(): MutableList<TaskObject>{
        return this.listTask
    }

    fun setItem(list: MutableList<TaskObject>) {

        if (isFilter == true) {
            var taskOnList = arrayListOf<TaskObject>()
            for (item in list) {
                if (item.TaskState == true) {
                    taskOnList.add(item)
                }
            }
            this.listTask = taskOnList
            notifyDataSetChanged()
        } else {
            this.listTask = list
            notifyDataSetChanged()

        }

    }

    fun setSetter(setter: DisableViewSetter) {
        this.setter = setter
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listTask.size
    }

    interface DisableViewSetter {
        fun disableView() {
        }

        fun deleteTask(id: Long, deleteResponse: DeleteResponse){
        }
    }

    interface DeleteResponse{
        fun deleteResponse(){}
    }

}
