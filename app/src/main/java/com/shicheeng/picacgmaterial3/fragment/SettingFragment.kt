package com.shicheeng.picacgmaterial3.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.api.FileUtils

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preference: Preference = findPreference("CLEAN_MEMORY")!!
        preference.summary =
            getString(R.string.summary_clear_cache, FileUtils.getCacheSize(requireContext()))

        preference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            requireContext().cacheDir.deleteRecursively()
            preference.summary =
                getString(R.string.summary_clear_cache, FileUtils.getCacheSize(requireContext()))

            true
        }
    }

}