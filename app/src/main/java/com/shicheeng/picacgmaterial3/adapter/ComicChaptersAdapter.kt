package com.shicheeng.picacgmaterial3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.data.ComicChpaterItemData

class ComicChaptersAdapter(private val list: MutableList<ComicChpaterItemData>) :
    RecyclerView.Adapter<ComicChaptersAdapter.ChapterHolder>() {

    private lateinit var onClick: (position: Int) -> Unit

    class ChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.chapter_item_title)
        val time: TextView = itemView.findViewById(R.id.chapter_item_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comic_info_chapter, parent, false)
        return ChapterHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterHolder, position: Int) {
        holder.title.text = list[position].title
        holder.time.text = list[position].time
        val lp: ViewGroup.LayoutParams = holder.itemView.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            val fl: FlexboxLayoutManager.LayoutParams = lp
            fl.flexGrow = 1.0f
        }
        holder.itemView.setOnClickListener {
            onClick.invoke(position)
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnItemClickListener(l: (position: Int) -> Unit) {
        this.onClick = l
    }
}