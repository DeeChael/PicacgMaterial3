package com.shicheeng.picacgmaterial3.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.RankChipAdapter.ChipHolder

class RankChipAdapter(private val list: MutableList<String>) : RecyclerView.Adapter<ChipHolder>() {
    class ChipHolder(itemView: View) : ViewHolder(itemView) {
        val chip: Chip = itemView.findViewById(R.id.rank_tag_chip)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rank_item_chip, parent, false)
        return ChipHolder(view)
    }

    override fun onBindViewHolder(holder: ChipHolder, position: Int) {
        holder.chip.text = list[position]
        val lp: ViewGroup.LayoutParams = holder.chip.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            val fl: FlexboxLayoutManager.LayoutParams = lp
            fl.flexGrow = 1.0f

        }
    }

    override fun getItemCount(): Int = list.size
}