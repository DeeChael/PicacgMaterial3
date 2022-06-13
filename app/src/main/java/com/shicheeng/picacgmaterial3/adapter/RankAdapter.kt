package com.shicheeng.picacgmaterial3.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.data.RankData
import com.shicheeng.picacgmaterial3.main.ComicInfoActivity
import com.shicheeng.picacgmaterial3.widget.setChips

class RankAdapter(private val list: MutableList<RankData>) :
    RecyclerView.Adapter<RankAdapter.RankHolder>() {

    class RankHolder(itemView: View) : ViewHolder(itemView) {
        val textTitle: AppCompatTextView = itemView.findViewById(R.id.rank_comic_title)
        val textAuthor: TextView = itemView.findViewById(R.id.rank_comic_author)
        val textCount: TextView = itemView.findViewById(R.id.rank_comic_leaderboard_count)
        val tagsList: ChipGroup = itemView.findViewById(R.id.rank_comic_tags_list)
        val textLike: TextView = itemView.findViewById(R.id.decs_leader)
        val thumbImage: ImageView = itemView.findViewById(R.id.rank_comic_thumb)
        val parentView: MaterialCardView = itemView.findViewById(R.id.rank_comic_item_parent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rank_item, parent, false)
        return RankHolder(view)
    }

    override fun onBindViewHolder(holder: RankHolder, position: Int) {
        holder.apply {
            textTitle.text = list[position].title
            textAuthor.text = list[position].author
            textCount.text = list[position].leaderboardCount.toString()
            textLike.text = list[position].decType
            Glide.with(holder.itemView.context).load(list[position].url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(thumbImage)
        }

        holder.tagsList.apply {
            setChips(list[position].categories, styleId = R.attr.chipTagsStyle)
        }

        holder.parentView.setOnClickListener {
            val intent = Intent()
            intent.setClass(holder.itemView.context, ComicInfoActivity::class.java)
            intent.putExtra("COMIC_ID", list[position]._id)
            holder.itemView.context.startActivity(intent)
        }


    }


    override fun getItemCount(): Int = list.size

}