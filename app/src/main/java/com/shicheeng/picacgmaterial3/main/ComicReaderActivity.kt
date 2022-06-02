package com.shicheeng.picacgmaterial3.main

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.pagers.ReaderPagerAdapter
import com.shicheeng.picacgmaterial3.databinding.ActivityRaderBinding
import com.shicheeng.picacgmaterial3.fragment.reader.ReaderPagerFragment
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.viewmodel.ComicReaderViewModel

@Suppress("DEPRECATION")
class ComicReaderActivity : AppActivity() {

    private lateinit var binding: ActivityRaderBinding
    private var isShowBar: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaderBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        paddingUp(viewRoot, binding.comicReaderAppBar, binding.comicReaderBottomBar)

        val viewModel: ComicReaderViewModel by viewModels()
        val token = token()!!
        val comicId = intent.getStringExtra("COMIC_ID")!!
        val orderList = intent.getIntegerArrayListExtra("CHAPTER_LIST")!!
        var position = intent.getIntExtra("POSITION", 0)
        val supportingTitle = intent.getStringExtra("TITLE")
        val page = 1

        binding.comicReaderToolBar.title = supportingTitle ?: "未知标题"
        setSupportActionBar(binding.comicReaderToolBar)
        viewModel.loadComicUri(comicId, orderList[position], page, token)
        viewModel.loadComicsUrls(comicId, orderList[position], token)

        viewModel.comicPictureData.observe(this) {


            val comicEps = JsonParser.parseString(it).asJsonObject["data"]
                .asJsonObject["ep"].asJsonObject["title"].asString
            val totalPagers = JsonParser.parseString(it).asJsonObject["data"]
                .asJsonObject["pages"].asJsonObject["total"].asInt

            binding.comicReaderViewPagers.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {

                    super.onPageSelected(position)
                    binding.comicReaderBottomSubtitle.text =
                        getString(R.string.page_count,
                            (position + 1).toString(),
                            totalPagers.toString())
                }
            })

            binding.comicReaderBottomSubtitle.text = getString(R.string.page_count,
                1.toString(),
                totalPagers.toString())
            binding.comicReaderBottomTitle.text = comicEps
            binding.comicReaderBottomBtn.text = getString(R.string.next)
            binding.comicReaderBottomBtn.setOnClickListener {
                position -= 1
                //小判断
                if (position != -1) {
                    viewModel.loadComicUri(comicId, orderList[position], page, token)
                    viewModel.loadComicsUrls(comicId, orderList[position], token)

                } else {
                    Snackbar.make(viewRoot, "无下一章节", Snackbar.LENGTH_INDEFINITE)
                        .setAction("退出") {
                            finish()
                        }.show()
                }
            }

            hideSystemBars()
        }

        viewModel.comicPictureUrl.observe(this) {
            val list = ArrayList<Fragment>()
            it.forEach { url ->
                list.add(ReaderPagerFragment.newInstance(url))
            }

            val pagerAdapter = ReaderPagerAdapter(list, supportFragmentManager, this.lifecycle)
            binding.comicReaderViewPagers.apply {
                adapter = pagerAdapter
                offscreenPageLimit = 2
            }

            pagerAdapter.setOnPagerContentClickListener {
                toggleShowBar()
            }

        }

        binding.comicReaderToolBar.setNavigationOnClickListener {
            finish()
        }

        viewModel.comicLoadShow.observe(this) {
            binding.comicReaderShowingBar.showState(it)
        }

        viewModel.errorParserUrl.observe(this) {
            binding.comicReaderModernBtn.visibility = View.VISIBLE
            binding.comicReaderModernBtn.apply {
                showState(true)
                setTipText(getString(it))
                setOnClickListener {
                    showState(false)
                    binding.comicReaderShowingBar.showState(true)
                    binding.comicReaderShowingBar.setText("重新加载中")
                    viewModel.loadComicUri(comicId, orderList[position], page, token)
                    viewModel.loadComicsUrls(comicId, orderList[position], token)
                }
            }

        }

        viewModel.errorDateGet.observe(this) {
            binding.comicReaderBottomTitle.text = getString(it)
            binding.comicReaderBottomSubtitle.text = getString(it)
            binding.comicReaderBottomBtn.apply {
                text = getString(R.string.retry)
                setOnClickListener {
                    viewModel.loadComicUri(comicId, orderList[position], 1, token)
                }
            }

        }

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_N) {
            binding.comicReaderViewPagers.currentItem = +1
            return true
        } else if (keyCode == KeyEvent.KEYCODE_P) {
            binding.comicReaderViewPagers.currentItem = -1
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    //切换
    private fun toggleShowBar() {
        if (isShowBar) {
            hideSystemBars()
        } else {
            showSystemBar()
        }
    }

    //隐藏
    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= 30) {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(window.decorView) ?: return
            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            binding.root.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        supportActionBar!!.hide()
        isShowBar = false
        binding.comicReaderBottomBar.visibility = View.GONE
    }

    //显示
    private fun showSystemBar() {
        if (Build.VERSION.SDK_INT >= 30) {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(window.decorView) ?: return
            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Show both the status bar and the navigation bar
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            binding.root.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        }

        supportActionBar!!.show()
        isShowBar = true
        binding.comicReaderBottomBar.visibility = View.VISIBLE
    }


}