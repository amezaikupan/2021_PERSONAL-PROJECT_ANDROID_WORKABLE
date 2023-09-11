package com.improver.workable.fragtaskprogres.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.databinding.ItemProgressBinding
import com.improver.workable.fragpostrun.adapter.PhotoAdapter


class ProgressViewHolder(
    private val binding: ItemProgressBinding,
    var photoListenner: PhotoAdapter.OnPhotoEnlargeListener

) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("WrongConstant")
    fun bind(progressObject: ProgressObject) {

        var dateAndMonth = progressObject.progressDate.split("/")
        binding.deadlineDate.text = dateAndMonth[0]
        binding.deadlineMonth.text = dateAndMonth[1]

        binding.noteTv.text = progressObject.progressNote

        var photoAdapter = PhotoAdapter ()
        photoAdapter.setItem(progressObject.photoList)
        photoAdapter.setListener(photoListenner)

        binding.imgPg.layoutManager = LinearLayoutManager(binding.imgPg.context, LinearLayout.HORIZONTAL, false)
        binding.imgPg.setOnFlingListener(null)
        PagerSnapHelper().attachToRecyclerView(binding.imgPg)

        binding.imgPg.adapter = photoAdapter

    }

}
