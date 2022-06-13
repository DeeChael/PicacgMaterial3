package com.shicheeng.picacgmaterial3.inapp

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.shicheeng.picacgmaterial3.api.Utils

open class AppActivity : AppCompatActivity() {

    fun paddingUp(rootView: View, app: AppBarLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, wic: WindowInsetsCompat ->

            val insets = wic.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd = insets.right
                marginStart = insets.left
            }
            app.updatePadding(top = insets.top)

            WindowInsetsCompat.CONSUMED
        }
    }

    fun paddingUp(rootView: View, app: AppBarLayout, bottomView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, wic: WindowInsetsCompat ->

            val insets = wic.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd = insets.right
                marginStart = insets.left
            }
            app.updatePadding(top = insets.top)
            bottomView.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    fun token(): String? {
        val shareFile = this.getSharedPreferences(Utils.preferenceFileKey, MODE_PRIVATE)
        return shareFile.getString(Utils.key, "NO_KEY")
    }


}