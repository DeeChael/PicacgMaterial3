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


fun CircularProgressIndicator.setShowWithBoolean(boolean: Boolean) {
    visibility = if (boolean) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

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

