package com.shicheeng.picacgmaterial3.widget

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * 扩展了原本的圆形指示器，可以直接使用布尔值来控制消失显示
 */
fun CircularProgressIndicator.setShowWithBoolean(boolean: Boolean) {
    visibility = if (boolean) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * 扩展了原本的圆形指示器，可以直接使用布尔值来控制消失显示
 */
fun LinearProgressIndicator.setShowWithBoolean(boolean: Boolean) {
    visibility = if (boolean) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun RecyclerView.setAdapterWithLinearLayout(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
    val lineV = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    setAdapter(adapter)
    layoutManager = lineV
}

fun RecyclerView.setAdapterWithHorizontalLayout(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
    val lineV = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    setAdapter(adapter)
    layoutManager = lineV
}

fun LinearLayout.setShowState(boolean: Boolean) {
    visibility = if (boolean) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun RecyclerView.setAdapterAndManager(
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    manager: FlexboxLayoutManager,
) {
    setAdapter(adapter)
    layoutManager = manager
}

/**
 * Replaces chips in a ChipGroup.
 * Copy from https://github.com/tachiyomiorg/tachiyomi/blob/master/app/src/main/java/eu/kanade/tachiyomi/util/view/ViewExtensions.kt
 *
 * @param items List of strings that are shown as individual chips.
 * @param onClick Optional on click listener for each chip.
 * @param onLongClick Optional on long click listener for each chip.
 */
fun ChipGroup.setChips(
    items: List<String>?,
    onClick: ((item: String) -> Unit)? = null,
    onLongClick: ((item: String) -> Unit)? = null,
    styleId: Int,
) {
    removeAllViews()

    items?.forEach { item ->
        val chip = Chip(context, null, styleId).apply {
            text = item

            if (onClick != null) {
                setOnClickListener { onClick(item) }
            }
            if (onLongClick != null) {
                setOnLongClickListener { onLongClick(item); true }
            }
        }

        addView(chip)
    }
}

fun RecyclerView.setOnScrollingAction(onScrolling: () -> Unit) {

    addOnScrollListener(object : RecyclerView.OnScrollListener() {

        var isScrolling = false
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val llm = recyclerView.layoutManager as LinearLayoutManager
            val items = llm.itemCount
            val lastItem = llm.findLastVisibleItemPosition()

            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if (isScrolling && lastItem == (items - 1)) {
                    onScrolling()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            isScrolling = dy > 0
        }

    })
}

