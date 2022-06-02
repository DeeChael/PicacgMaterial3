package com.shicheeng.picacgmaterial3.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.databinding.WidgetModernProgressIndicatorBinding

class ModernCircularProgressIndicator @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    private val binding =
        WidgetModernProgressIndicatorBinding
            .inflate(LayoutInflater.from(context), this, true)


    fun showState(boolean: Boolean) {
        binding.root.visibility = if (boolean) {
            VISIBLE
        } else {
            GONE
        }
    }

    fun setText(text: String) {
        binding.widgetModernIndicatorText.text = text
    }

    fun setTextColor(resID: Int) {
        binding.widgetModernIndicatorText.setTextColor(resID)
    }

    init {
        context.theme.obtainStyledAttributes(attributeSet,
            R.styleable.ModernCircularProgressIndicator,
            0,
            0).apply {

            binding.widgetModernIndicatorText.text =
                getString(R.styleable.ModernCircularProgressIndicator_showingText)
            binding.widgetModernIndicatorText
                .setTextColor(
                    getColor(R.styleable.ModernCircularProgressIndicator_showingTextColor,
                        Color.BLACK))
        }
    }

}