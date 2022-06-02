package com.shicheeng.picacgmaterial3.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.adapter.FavoriteComicAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.FavoriteItemData
import com.shicheeng.picacgmaterial3.databinding.ActivityFavoriteComicBinding
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.viewmodel.FavoriteViewModel
import com.shicheeng.picacgmaterial3.widget.setAdapterWithLinearLayout
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean

class FavoriteComicActivity : AppActivity() {

    private lateinit var binding: ActivityFavoriteComicBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteComicBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        paddingUp(viewRoot, binding.favoriteComicAppBar)
        binding.favoriteComicModernButton.showState(false)
        val shareFile = this.getSharedPreferences(Utils.preferenceFileKey, MODE_PRIVATE)
        val token = shareFile.getString(Utils.key, "NO_KEY")!!
        val viewModel: FavoriteViewModel by viewModels()
        var page = 1
        viewModel.favoriteBooks(page, token)
        val listHost = ArrayList<FavoriteItemData>()
        val adapterL = FavoriteComicAdapter(listHost)

        //数据接收
        viewModel.favoriteData.observe(this) {
            val favoriteJson = JsonParser.parseString(it)
                .asJsonObject["data"]
                .asJsonObject["comics"]
                .asJsonObject["docs"].asJsonArray

            val pageLimit = JsonParser.parseString(it)
                .asJsonObject["data"]
                .asJsonObject["comics"].asJsonObject["pages"].asInt

            val list = jsonToListArray(favoriteJson)
            listHost.addAll(list)
            adapterL.notifyDataSetChanged()

            binding.favoriteComicRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                var isNextScroll = false

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val lm = recyclerView.layoutManager as LinearLayoutManager
                    val lastCount = lm.findLastCompletelyVisibleItemPosition()
                    val itemCount = lm.itemCount

                    if (lastCount == itemCount - 1 && newState == RecyclerView.SCROLL_STATE_DRAGGING
                        && isNextScroll
                    ) {
                        page += 1
                        if (page < pageLimit + 1) {
                            viewModel.favoriteBooks(page, token)
                            binding.favoriteComicBottomIndicator.setShowWithBoolean(true)
                        } else {
                            Snackbar.make(viewRoot, "已经是最后一页了", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    isNextScroll = dy > 0
                }

            })
        }


        binding.favoriteComicRecyclerView.setAdapterWithLinearLayout(adapterL)
        binding.favoriteComicRecyclerView.addItemDecoration(DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL))


        //完成接收
        viewModel.loadProgressCir.observe(this) {
            binding.favoriteComicIndication.setShowWithBoolean(it)
        }

        binding.favoriteComicToolBar.setNavigationOnClickListener {
            finish()
        }

        //网络错误判断接受
        viewModel.loadSee.observe(this) {
            binding.favoriteComicModernButton.apply {
                setTipText(it)
                showState(true)
                setOnClickListener {
                    viewModel.favoriteBooks(1, token)
                    showState(false)
                    binding.favoriteComicIndication.setShowWithBoolean(true)
                }
            }

        }

        //底部线性指示器
        viewModel.bottomBar.observe(this) {
            binding.favoriteComicBottomIndicator.setShowWithBoolean(it)
        }

    }


    /**
     * 封装此方法以用于加载下一个
     *
     */
    private fun jsonToListArray(jsonThing: JsonArray): ArrayList<FavoriteItemData> {
        val listItem = ArrayList<FavoriteItemData>()

        for (jt in jsonThing) {
            val json = jt.asJsonObject
            val id = json["_id"].asString
            val title = json["title"].asString
            val author = json["author"].asString
            val url = json["thumb"].asJsonObject.let { jsonObject ->
                Utils.getComicRankImage(jsonObject["fileServer"].asString,
                    jsonObject["path"].asString)
            }
            val categoryData = ArrayList<String>()
            json["categories"].asJsonArray.forEach {
                categoryData.add(it.asString)
            }

            val item = FavoriteItemData(id, title, author, url, categoryData)
            listItem.add(item)
        }
        return listItem
    }

}