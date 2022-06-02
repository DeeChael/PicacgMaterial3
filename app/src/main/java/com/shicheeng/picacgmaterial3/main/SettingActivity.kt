package com.shicheeng.picacgmaterial3.main

import android.os.Bundle
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.databinding.ActivitySettingBinding
import com.shicheeng.picacgmaterial3.fragment.SettingFragment
import com.shicheeng.picacgmaterial3.inapp.AppActivity

class SettingActivity : AppActivity() {

    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
        paddingUp(rootView, binding.settingAppBar)
        supportFragmentManager.beginTransaction()
            .replace(R.id.setting_container, SettingFragment())
            .commit()
        binding.settingBar.setNavigationOnClickListener {
            finish()
        }
    }
}