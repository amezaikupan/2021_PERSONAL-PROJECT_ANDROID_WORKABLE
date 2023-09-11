package com.improver.workable.fragtaskprogres.adapter.progressheader

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.R
import com.improver.workable.databinding.ItemProgressHeaderBinding
import com.improver.workable.fragactmain.adapter.TaskObject
import com.improver.workable.fragtaskprogres.ProgressFragmentDirections
import com.google.android.material.chip.Chip


class ProgressHeaderAdapter() :
    RecyclerView.Adapter<ProgressHeaderViewHolder>() {

    private lateinit var listProgress: List<TaskObject>
    private lateinit var listener: OnChipItemCick
    private lateinit var inListenter : OnInResponse



    fun setItem(listProgress: List<TaskObject>) {
        this.listProgress = listProgress
        notifyDataSetChanged()

    }

    fun setListener(listener: OnChipItemCick) {
        this.listener = listener
    }




    override fun onBindViewHolder(holder: ProgressHeaderViewHolder, position: Int) {
        holder.bind(listProgress[position])

        inListenter = object : OnInResponse{
            override fun finishedTask() {
                holder.finishedTask()
            }
        }

        var task = listProgress[position]

        holder.itemView.findViewById<Button>(R.id.do_task_button).setOnClickListener {
            //navigate to run fragment
                var action = ProgressFragmentDirections.actionProgressFragmentToRunTaskFragment(
                    task.taskId.toString(),
                    task.taskDeadline)
                holder.itemView?.findNavController()?.navigate(action)
            //---------------------------------
        }

        holder.itemView.findViewById<Chip>(R.id.chip_button).setOnClickListener{
            listener.onChipDialog()
        }

        holder.itemView.findViewById<Chip>(R.id.finish_task_button).setOnClickListener{
            listener.onFinishTask(inListenter)

        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressHeaderViewHolder {
        return ProgressHeaderViewHolder(
            ItemProgressHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
    }

    override fun getItemCount(): Int {
        return listProgress.size
    }


    interface OnChipItemCick {
        fun onChipDialog(){
        }
        fun onFinishTask(onInResponse: OnInResponse){
        }
    }

    interface OnInResponse{
        fun finishedTask()
    }
}





