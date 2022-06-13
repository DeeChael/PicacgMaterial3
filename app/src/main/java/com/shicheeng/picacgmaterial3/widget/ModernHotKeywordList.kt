package com.shicheeng.picacgmaterial3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.databinding.WidgetHotKeywordListBinding

class ModernHotKeywordList @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    private val binding =
        WidgetHotKeywordListBinding
            .inflate(LayoutInflater.from(context), this, true)

    fun setChips(list: List<String>, onClick: ((v: String) -> Unit)? = null) {
        binding.searchKeywordChipGroup.removeAllViews()
        list.forEach { s ->
            val chip = Chip(context, null, R.attr.chipTagsBigStyle).apply {
                text = s
                if (onClick != null) {
                    setOnClickListener { onClick(s) }
                }
            }
            binding.searchKeywordChipGroup.addView(chip)
        }
    }

    fun setShowState(boolean: Boolean) {
        binding.root.setShowState(boolean)
    }


}