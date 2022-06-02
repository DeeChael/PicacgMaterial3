package com.shicheeng.picacgmaterial3.adapter.pagers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class ReaderPagerAdapter(
    private val list: List<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private lateinit var l: (position: Int) -> Unit

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment = list[position]

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        super.onBindViewHolder(holder, position, payloads)

        holder.itemView.setOnClickListener {
            l.invoke(position)
        }
    }

    fun setOnPagerContentClickListener(l: (position: Int) -> Unit) {
        this.l = l
    }

}