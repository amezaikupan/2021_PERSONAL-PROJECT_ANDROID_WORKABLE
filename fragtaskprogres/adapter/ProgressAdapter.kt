package com.improver.workable.fragtaskprogres.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.databinding.ItemProgressBinding
import com.improver.workable.fragpostrun.adapter.PhotoAdapter

class ProgressAdapter():
    RecyclerView.Adapter<ProgressViewHolder>() {

    private lateinit var listProgress: List<ProgressObject>
    private lateinit var phototListener: PhotoAdapter.OnPhotoEnlargeListener

    fun setPhotoListener(listener: PhotoAdapter.OnPhotoEnlargeListener){
        this.phototListener = listener
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bind(listProgress[position])
    }


    fun setItem(listProgress: List<ProgressObject>) {
        this.listProgress = listProgress

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        return ProgressViewHolder(
            ItemProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            phototListener
        )
    }

    override fun getItemCount(): Int {
        return listProgress.size
    }





}