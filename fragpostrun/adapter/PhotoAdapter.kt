package com.improver.workable.fragpostrun.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.R
import com.improver.workable.databinding.ItemPhotoBinding


class PhotoAdapter() :
    RecyclerView.Adapter<PhotoViewHolder>() {

    private lateinit var listTask: ArrayList<PhotoObject>
    private lateinit var listener: OnPhotoEnlargeListener
    lateinit var imageView :ImageView

    fun setItem(list: ArrayList<PhotoObject>) {
        this.listTask = list
        notifyDataSetChanged()
    }


    fun setListener(listener: OnPhotoEnlargeListener) {
        this.listener = listener
    }
    var clickedPhoto: String = ""

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(listTask[position])

        holder.itemView.findViewById<ImageView>(R.id.photo).setOnClickListener {it ->
            imageView = it as ImageView
            clickedPhoto = listTask[holder.layoutPosition].image

            listener.onPhotoEnlargeListener(clickedPhoto)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listTask.size
    }

    interface OnPhotoEnlargeListener {
        fun onPhotoEnlargeListener(photo: String){
        }
    }
    
}