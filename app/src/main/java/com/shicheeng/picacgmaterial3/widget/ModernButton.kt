package com.shicheeng.picacgmaterial3.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.R.styleable.ShihCheengModernButton
import com.shicheeng.picacgmaterial3.databinding.WidgetModernButtonBinding

@SuppressLint("Recycle", "CustomViewStyleable")
class ModernButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet,
    @AttrRes defStyleAttr: Int = 0,
    @AttrRes defStyleRes: Int = 0,
) : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    private val binding =
        WidgetModernButtonBinding
            .inflate(LayoutInflater.from(context), this, true)


    fun setTipText(text:String){
        binding.tipText.text = text
    }

    fun showState(boolean: Boolean) {
        binding.root.visibility = if (boolean) {
            VISIBLE
        } else {
            GONE
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.tipButton.setOnClickListener(l)
    }


    init {
        context.theme.obtainStyledAttributes(attributeSet,
            ShihCheengModernButton,
            defStyleAttr,
            0).apply {
            try {
                binding.tipButton.text = getString(R.styleable.ShihCheengModernButton_buttonText)
                binding.tipText.text = getString(R.styleable.ShihCheengModernButton_tipText)
            } finally {
                recycle()
            }
        }
    }


}