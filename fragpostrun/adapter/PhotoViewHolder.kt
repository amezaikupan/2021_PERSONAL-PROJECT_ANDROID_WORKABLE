package com.improver.workable.fragpostrun.adapter

import android.content.res.Resources
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.improver.workable.databinding.ItemPhotoBinding

class PhotoViewHolder(
    private val binding: ItemPhotoBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(photo: PhotoObject) {
        var uri = photo.image.toUri()
        var thisPhoto = binding.photo

        Glide.with(thisPhoto.context).load(uri)
            .override(Resources.getSystem().getDisplayMetrics().widthPixels)
            .thumbnail(0.05f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(thisPhoto)


    }
    init {


    }

}
