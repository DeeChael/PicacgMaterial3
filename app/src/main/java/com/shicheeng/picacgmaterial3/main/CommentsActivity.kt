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
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.CommentAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.ComicCommentItemData
import com.shicheeng.picacgmaterial3.databinding.ActivityCommentBinding
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.viewmodel.CommentViewModel
import com.shicheeng.picacgmaterial3.widget.setAdapterWithLinearLayout
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean

class CommentsActivity : AppActivity() {

    private lateinit var binding: ActivityCommentBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        paddingUp(viewRoot, binding.commentAppBarLayout)
        val title = intent.getStringExtra("TITLE")
        val comicId = intent.getStringExtra("COMIC_ID")!!
        val token = token()!!
        var page = 1
        var pages = 0
        binding.commentToolBar.apply {
            setNavigationOnClickListener {
                finish()
            }
        }.subtitle = if (title.isNullOrBlank()) "未知标题" else title
        val viewModel: CommentViewModel by viewModels()
        viewModel.loadData(comicId, page, token)

        //加载数据
        val array = ArrayList<ComicCommentItemData>()
        val commentAdapter = CommentAdapter(array)
        binding.commentRecyclerView.setAdapterWithLinearLayout(commentAdapter)
        binding.commentRecyclerView.addItemDecoration(DividerItemDecoration(this,
            RecyclerView.VERTICAL))
        viewModel.data.observe(this) {
            array.addAll(parserData(it))
            commentAdapter.notifyDataSetChanged()
        }

        viewModel.pages.observe(this) {
            pages = it
        }

        binding.commentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var onScrolling = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val itemCounts = layoutManager.itemCount
                val lastCount = layoutManager.findLastVisibleItemPosition()
                if (onScrolling && newState == RecyclerView.SCROLL_STATE_DRAGGING && lastCount + 1 == itemCounts) {
                    page += 1
                    if (page <= pages) {
                        viewModel.loadData(comicId, page, token)
                    } else {
                        Snackbar.make(viewRoot, getString(R.string.no_more), Snackbar.LENGTH_LONG)
                            .show()
                        page = pages + 1
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrolling = dy > 0
            }
        })

        viewModel.errorMessage.observe(this) {
            binding.commentModernButton.apply {
                showState(true)
                setTipText(it)
                setOnClickListener {
                    showState(false)
                    viewModel.loadData(comicId, page, token)
                    binding.commentCircularIndicator.setShowWithBoolean(true)
                }
            }
        }

        viewModel.showState.observe(this) {
            binding.commentCircularIndicator.setShowWithBoolean(it)
        }

    }

    private fun parserData(ja: JsonArray): ArrayList<ComicCommentItemData> {
        val ayl = ArrayList<ComicCommentItemData>()
        for (element in ja) {
            val ob = element.asJsonObject
            val user = ob["_user"].asJsonObject
            val name = user["name"].asString
            val level = user["level"].asInt
            val url = if (user.keySet().contains("avatar")) {
                Utils.getComicRankImage(
                    user["avatar"].asJsonObject["fileServer"].asString,
                    user["avatar"].asJsonObject["path"].asString
                )
            } else {
                null
            }
            val slogan = if (user.keySet().contains("slogan")) user["slogan"].asString else null
            val time = Utils.formatTime(ob["created_at"].asString)
            val like = ob["likesCount"].asInt
            val content = ob["content"].asString
            val commentData = ComicCommentItemData(name,
                slogan,
                level.toString(),
                url,
                content,
                like,
                time)
            ayl.add(commentData)
        }
        return ayl
    }

}