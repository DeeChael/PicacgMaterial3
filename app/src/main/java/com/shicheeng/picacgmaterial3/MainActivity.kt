package com.shicheeng.picacgmaterial3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.shicheeng.picacgmaterial3.R.id.*
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.databinding.ActivityMainBinding
import com.shicheeng.picacgmaterial3.fragment.FirstFragment
import com.shicheeng.picacgmaterial3.fragment.SecondFragment
import com.shicheeng.picacgmaterial3.fragment.ThirdFragment
import com.shicheeng.picacgmaterial3.fragment.login.LoginDialogFragment
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.main.SearchActivity
import com.shicheeng.picacgmaterial3.main.SettingActivity
import com.shicheeng.picacgmaterial3.viewmodel.MainViewModel

class MainActivity : AppActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val viewModel: MainViewModel by viewModels()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        paddingUp(binding.root, binding.mainAppLayout, binding.mainBottomNavigationBar)
        val pager2 = binding.mainContent.mainViewPager2

        val sharedPref = this.getSharedPreferences(Utils.preferenceFileKey, Context.MODE_PRIVATE)

        if (sharedPref.contains(Utils.key)) {
            // Log.d("WHERE", "onCreate: 从key获取")
            val token = sharedPref.getString(Utils.key, null)!!
            val listFragment = listOf(
                FirstFragment.newInstance(token),
                SecondFragment.newInstance(token),
                ThirdFragment.newInstance(token)
            )
            binding.progressOnGettingTokenBar.visibility = View.GONE
            val mAdapter = MainAdapter(listFragment, supportFragmentManager, this.lifecycle)
            pager2.apply {
                adapter = mAdapter
            }

        } else {
            val loginDialog = LoginDialogFragment()
            loginDialog.isCancelable = false
            loginDialog.show(supportFragmentManager, LoginDialogFragment.TAG)
            viewModel.dataToken.observe(loginDialog) {
                val listFragment = listOf(
                    FirstFragment.newInstance(it),
                    SecondFragment.newInstance(it),
                    ThirdFragment.newInstance(it)
                )
                loginDialog.dismiss()
                binding.progressOnGettingTokenBar.visibility = View.GONE
                val mAdapter = MainAdapter(listFragment, supportFragmentManager, this.lifecycle)
                pager2.apply {
                    adapter = mAdapter
                }

            }


        }

        viewModel.loginOutError.observe(this) {
            val dialogFragment = LoginDialogFragment()
            dialogFragment.isCancelable = false
            dialogFragment.show(supportFragmentManager, LoginDialogFragment.TAG)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        binding.mainBottomNavigationBar.setOnItemSelectedListener {

            when (it.itemId) {
                all_category -> pager2.currentItem = 0
                all_leaderboard -> {
                    pager2.currentItem = 1
                }
                me_collect -> pager2.currentItem = 2
            }
            false
        }

        pager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.mainBottomNavigationBar.menu.getItem(position).isChecked = true
                if (position == 1) {
                    binding.toolbar.subtitle = getString(R.string.limited_show)
                } else {
                    binding.toolbar.subtitle = null
                }
            }

        })

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            action_settings -> {
                val setting = Intent(this, SettingActivity::class.java)
                startActivity(setting)
                true
            }
            action_search -> {
                val intent = Intent()
                intent.setClass(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    class MainAdapter(
        private val list: List<Fragment>,
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = list.size

        override fun createFragment(position: Int): Fragment = list[position]
    }


}