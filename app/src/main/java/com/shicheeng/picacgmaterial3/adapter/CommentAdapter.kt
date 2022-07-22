package com.shicheeng.picacgmaterial3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.data.ComicCommentItemData
import com.shicheeng.picacgmaterial3.databinding.ItemCommentBinding

class CommentAdapter(private val list: MutableList<ComicCommentItemData>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private lateinit var binding: ItemCommentBinding

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slogan = binding.commentBarUserSlogan
        val name = binding.commentBarUserName
        val avatar = binding.commentBarUserAvatar
        val level = binding.commentBarUserLevel
        val likeUser = binding.likeCountComment
        val content = binding.commentBodyText
        val timeS = binding.commentBodyTextTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.level.text = list[position].level
        holder.name.text = list[position].name
        holder.slogan.text =
            if (list[position].slogan.isNullOrBlank()) "无签名"
            else list[position].slogan
        holder.likeUser.text = holder.itemView.context.getString(
            R.string.like_count_comment,
            list[position].likeCount.toString(),
        )
        holder.content.text = list[position].content
        val url = list[position].url
        if (url.isNullOrBlank()) {
            Glide.with(holder.itemView).load(R.mipmap.ic_launcher_pic).into(holder.avatar)
        } else {
            Glide.with(holder.itemView).load(url).into(holder.avatar)
        }
        holder.timeS.text = list[position].time
    }

    override fun getItemCount(): Int = list.size
}