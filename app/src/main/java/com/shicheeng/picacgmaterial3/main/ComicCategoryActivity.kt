package com.shicheeng.picacgmaterial3.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.RankAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.RankData
import com.shicheeng.picacgmaterial3.databinding.ActivityComicsBinding
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.viewmodel.ComicsViewModel
import com.shicheeng.picacgmaterial3.widget.setAdapterWithLinearLayout
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean

class ComicCategoryActivity : AppActivity() {

    private lateinit var binding: ActivityComicsBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComicsBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        val viewModel: ComicsViewModel by viewModels()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        paddingUp(viewRoot, binding.comicsAppBar)
        var keyWord = intent.getStringExtra("TITLE")!!
        val token = token()!!
        binding.comicsToolBar.title = getString(R.string.comics_title, keyWord)
        var page = 1
        var order = Utils().order[0]!!

        viewModel.comics(page, keyWord, order, token)

        val listHost = ArrayList<RankData>()
        val adapterW = RankAdapter(listHost)
        viewModel.categoryData.observe(this) {
            val json = JsonParser.parseString(it)
                .asJsonObject["data"]
                .asJsonObject["comics"]
                .asJsonObject["docs"].asJsonArray

            val list = jsonToData(json)
            adapterW.notifyDataSetChanged()
            listHost.addAll(list)
        }
        binding.comicsRecyclerView.setAdapterWithLinearLayout(adapterW)

        binding.comicsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var isScroll = false
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isScroll = dy > 0
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLa = recyclerView.layoutManager as LinearLayoutManager
                val itemCount = linearLa.itemCount
                val lastCount = linearLa.findLastVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastCount == itemCount - 1 && isScroll) {
                        page += 1
                        viewModel.comics(page, keyWord, order, token)
                        viewModel.commonBarShow(true)
                    }
                }
            }

        })

        viewModel.indication.observe(this) {
            binding.comicsCircularIndicator.setShowWithBoolean(it)
        }

        viewModel.commonLoadingBar.observe(this) {
            binding.comicsBottomBar.setShowWithBoolean(it)
        }

        binding.comicsToolBar.setNavigationOnClickListener {
            finish()
        }

    }


    /**
     * 包装成函数
     * @param json [JsonArray] 数据输入
     */
    private fun jsonToData(json: JsonArray): ArrayList<RankData> {
        val list = ArrayList<RankData>()

        for (element in json) {
            val docs = element.asJsonObject
            val id = docs["_id"].asString
            val title = docs["title"].asString
            val author = docs["author"].asString
            val likesCount = docs["likesCount"].asInt
            val url = docs["thumb"].asJsonObject.let { it1 ->
                Utils.getComicRankImage(it1["fileServer"].asString, it1["path"].asString)
            }
            val mutableList = ArrayList<String>()
            docs["categories"].asJsonArray.forEach { jsonElement ->
                mutableList.add(jsonElement.asString)
            }
            val rankData = RankData(
                id,
                title,
                author,
                url,
                mutableList,
                likesCount,
                getString(R.string.like_count)
            )
            list.add(rankData)
        }

        return list
    }

}