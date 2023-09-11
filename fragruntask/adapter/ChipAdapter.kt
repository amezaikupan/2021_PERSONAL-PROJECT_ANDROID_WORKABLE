package com.improver.workable.fragruntask.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.R
import com.improver.workable.databinding.ItemChipBinding

class ChipAdapter :
    RecyclerView.Adapter<ChipViewHolder>() {

    private lateinit var listTask: List<ChipObject>

    var stateList = arrayListOf<Long>()

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(listTask[position])

        holder.itemView.findViewById<CheckBox>(R.id.checkbox).setOnCheckedChangeListener { _, _ ->
            var positionCheck = listTask[holder.layoutPosition].chipID

            if (holder.itemView.findViewById<CheckBox>(R.id.checkbox).isChecked == true){
                stateList.add(positionCheck)
                Log.v("ChipAdapter", "Check box position" + positionCheck)
            }else{
                stateList.remove(positionCheck)
            }

        }

    }

    fun setItem(list: List<ChipObject>) {
        this.listTask = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        return ChipViewHolder(
            ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listTask.size
    }



}