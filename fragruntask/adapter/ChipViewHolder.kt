package com.improver.workable.fragruntask.adapter

import androidx.recyclerview.widget.RecyclerView
import com.improver.workable.databinding.ItemChipBinding


class ChipViewHolder(
    private val binding: ItemChipBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(chip: ChipObject) {

        binding.chipText.text = chip.chipString
        binding.checkbox.isChecked = chip.chipState
    }

}
