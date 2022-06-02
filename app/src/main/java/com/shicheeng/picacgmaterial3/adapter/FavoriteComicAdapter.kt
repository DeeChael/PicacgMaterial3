package com.shicheeng.picacgmaterial3.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.data.FavoriteItemData
import com.shicheeng.picacgmaterial3.main.ComicInfoActivity
import com.shicheeng.picacgmaterial3.widget.setChips

class FavoriteComicAdapter(private val list: MutableList<FavoriteItemData>) :
    RecyclerView.Adapter<FavoriteComicAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.favorite_list_title)
        val authorText: TextView = itemView.findViewById(R.id.favorite_list_author)
        val categoryList: ChipGroup = itemView.findViewById(R.id.category_recycler)
        val thumb: ImageView = itemView.findViewById(R.id.favorite_list_thumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.favorite_comic_item, parent,
                false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            titleText.text = list[position].title
            authorText.text = list[position].author
            Glide.with(itemView).load(list[position].url).into(thumb)
            itemView.setOnClickListener {

                val intent = Intent()
                intent.setClass(holder.itemView.context, ComicInfoActivity::class.java)
                intent.putExtra("COMIC_ID", list[position]._id)
                holder.itemView.context.startActivity(intent)
            }
        }
        holder.categoryList.setChips(list[position].categories, styleId = R.attr.chipTagsStyle)

    }

    override fun getItemCount(): Int = list.size
}