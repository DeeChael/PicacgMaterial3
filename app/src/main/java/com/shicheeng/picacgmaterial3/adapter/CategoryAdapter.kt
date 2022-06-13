package com.shicheeng.picacgmaterial3.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.CategoryAdapter.CategoryViewHolder
import com.shicheeng.picacgmaterial3.data.CategoryData
import com.shicheeng.picacgmaterial3.main.ComicCategoryActivity

class CategoryAdapter(private val list: MutableList<CategoryData>, private val token: String) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.category_title)
        val icon: ImageView = itemView.findViewById(R.id.category_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.titleText.text = list[position].title
        Glide.with(holder.itemView.context).load(list[position].url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.icon)

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(holder.itemView.context, ComicCategoryActivity::class.java)
            intent.putExtra("TITLE", list[position].title)
            intent.putExtra("TOKEN", token)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size
}