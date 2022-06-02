package com.shicheeng.picacgmaterial3.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.ComicItemCommonAdapter.CommonHolder
import com.shicheeng.picacgmaterial3.data.ComicItemCommonData
import com.shicheeng.picacgmaterial3.main.ComicInfoActivity

class ComicItemCommonAdapter(private val list: MutableList<ComicItemCommonData>) :
    Adapter<CommonHolder>() {

    class CommonHolder(itemView: View) : ViewHolder(itemView) {
        val thumbImage: ImageView = itemView.findViewById(R.id.item_comic_image)
        val titleText: TextView = itemView.findViewById(R.id.item_comic_title)
        val authorText: TextView = itemView.findViewById(R.id.item_comic_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comic_item_common, parent, false)
        return CommonHolder(view)
    }

    override fun onBindViewHolder(holder: CommonHolder, position: Int) {
        holder.titleText.text = list[position].title
        holder.authorText.text = list[position].author
        holder.thumbImage.apply {
            Glide.with(this).load(list[position].url).into(this)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(holder.itemView.context, ComicInfoActivity::class.java)
            intent.putExtra("COMIC_ID", list[position]._id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}