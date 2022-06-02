package com.shicheeng.picacgmaterial3.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.shicheeng.picacgmaterial3.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
    }
}