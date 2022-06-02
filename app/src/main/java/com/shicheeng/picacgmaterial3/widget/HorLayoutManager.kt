package com.shicheeng.picacgmaterial3.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * This is no work
 */
// FIXME: This horizontal linear layout can`t using directly.Please help me.
class HorLayoutManager
constructor(context: Context) :
    LinearLayoutManager(context) {

    init {
        orientation = RecyclerView.HORIZONTAL
        reverseLayout = false
    }
}