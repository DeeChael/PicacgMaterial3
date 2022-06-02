package com.shicheeng.picacgmaterial3.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.google.android.flexbox.*
import com.google.android.material.chip.Chip
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.ComicChaptersAdapter
import com.shicheeng.picacgmaterial3.adapter.RankChipAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.ComicChpaterItemData
import com.shicheeng.picacgmaterial3.databinding.ActivtyComicInfoBinding
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.viewmodel.ComicInfoViewModel
import com.shicheeng.picacgmaterial3.widget.setAdapterAndManager
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean

class ComicInfoActivity : AppActivity() {

    private lateinit var binding: ActivtyComicInfoBinding
    private val viewModel: ComicInfoViewModel by viewModels()
    private var isExtend: Boolean = false
    private lateinit var titleComic: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivtyComicInfoBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        paddingUp(rootView = viewRoot, binding.comicAppBar, binding.comicInfoCreateTime)
        val token = token()!!
        val comicId = intent.getStringExtra("COMIC_ID")!!
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.getComicInfoData(comicId, token)
        viewModel.getComicChapterData(comicId, 1, token)
        titleComic = "未知标题"

        viewModel.comicInfoData.observe(this) {
            val data =
                JsonParser.parseString(it).asJsonObject["data"].asJsonObject["comic"].asJsonObject
            val titles = data["title"].asString.also { titleM ->
                titleComic = titleM
            }
            val description = data["description"].asString
            val thumbUrl = data["thumb"].asJsonObject.let { thumbW ->
                Utils.getComicRankImage(thumbW["fileServer"].asString, thumbW["path"].asString)
            }
            val author = data["author"].asString
            val chineseTeam = if (data.keySet().contains("chineseTeam")) {
                data["chineseTeam"].asString
            } else {
                "未知汉化组织"
            }
            val pagesCount = data["pagesCount"].asInt.toString()
            val updatedAt = data["updated_at"].asString.let { s ->
                Utils.formatTime(s)
            }
            val creator = data["_creator"].asJsonObject["name"].asString
            val createTime = data["created_at"].asString.let { createTime ->
                Utils.formatTime(createTime)
            }
            data["categories"].asJsonArray.apply {
                val ne = ArrayList<String>()
                val flexManager = FlexboxLayoutManager(this@ComicInfoActivity)
                flexManager.flexDirection = FlexDirection.ROW
                flexManager.justifyContent = JustifyContent.FLEX_START
                flexManager.alignItems = AlignItems.FLEX_START
                flexManager.flexWrap = FlexWrap.WRAP
                for (element in this) {
                    val categories = element.asString
                    ne.add(categories)
                }
                val ad = RankChipAdapter(ne)
                binding.comicInfoCategory.setAdapterAndManager(ad, flexManager)
            }
            data["tags"].asJsonArray.apply {
                for (i in this) {
                    val chip = Chip(this@ComicInfoActivity)
                    chip.text = i.asString
                    binding.comicInfoTagsList.addView(chip)
                }
            }

            binding.comicInfoTitle.text = getString(R.string.comic_title, titles, pagesCount)
            binding.comicInfoDescription.text = description
            binding.comicInfoDescriptionAll.text = description
            binding.comicInfoAuthor.text = author
            binding.comicInfoFanYiZhe.text = chineseTeam
            binding.comicInfoLastUpdate.text = getString(R.string.comic_last_update, updatedAt)
            binding.comicInfoCreator.text = getString(R.string.create_by, creator)
            binding.comicInfoCreateTime.text = createTime
            Glide.with(this).load(thumbUrl).into(binding.comicHeaderThumb)

        }

        viewModel.comicChapterData.observe(this) {
            val data =
                JsonParser.parseString(it).asJsonObject["data"].asJsonObject["eps"]
                    .asJsonObject["docs"].asJsonArray
            val flexboxLayoutManager = FlexboxLayoutManager(this)
            flexboxLayoutManager.flexDirection = FlexDirection.ROW
            flexboxLayoutManager.justifyContent = JustifyContent.FLEX_START
            val list = ArrayList<ComicChpaterItemData>()
            val allChapterList = ArrayList<Int>()

            for (datum in data) {
                val json = datum.asJsonObject
                val id = json["_id"].asString
                val title = json["title"].asString
                val order = json["order"].asInt
                val updateAt = json["updated_at"].asString.let { timeRfc9333 ->
                    Utils.formatTime(timeRfc9333)
                }
                val cid = ComicChpaterItemData(id, title, order, updateAt)
                list.add(cid)
                allChapterList.add(order)
            }
            val ad = ComicChaptersAdapter(list)
            binding.comicInfoChapterList.setAdapterAndManager(ad, flexboxLayoutManager)
            binding.comicInfoChapterList.isNestedScrollingEnabled = true
            ad.setOnItemClickListener { it1 ->
                val intent = Intent(this, ComicReaderActivity::class.java)
                intent.putExtra("COMIC_ID", comicId)
                intent.putExtra("CHAPTER_LIST", allChapterList)
                intent.putExtra("POSITION", it1)
                intent.putExtra("TITLE", titleComic)
                startActivity(intent)
            }

        }

        viewModel.comicInfoError.observe(this) {
            when (it) {
                Utils.ERROR_TIME_OUT -> {
                    binding.comicInfoTitle.text = "超时"
                }
                Utils.ERROR_LOADING -> {
                    binding.comicInfoTitle.text = "加载错误"
                }
            }
            binding.comicInfoAuthor.text = "点击重新加载"
            binding.comicInfoHeaderProgressIndicator.setShowWithBoolean(false)
            binding.comicHeaderLayout.setOnClickListener {
                viewModel.getComicInfoData(comicId, token)
                binding.comicInfoHeaderProgressIndicator.setShowWithBoolean(true)
            }

        }

        binding.comicInfoDescription.apply {
            if (this.lineCount < 2) {
                setOnClickListener {
                    toggle()
                }
            } else {
                return
            }
        }

        binding.comicInfoDescriptionAll.setOnClickListener {
            toggle()
        }

        binding.comicInfoToolBar.setNavigationOnClickListener {
            finish()
        }

        viewModel.show.observe(this) {
            binding.comicInfoHeaderProgressIndicator.setShowWithBoolean(it)
        }

        viewModel.chapterShow.observe(this) {
            binding.comicInfoProgressIndicator.setShowWithBoolean(it)
        }

    }

    private fun toggle() {
        if (isExtend) {
            collapseText()
        } else {
            extendText()
        }
    }

    private fun collapseText() {
        binding.comicInfoDescription.visibility = View.VISIBLE
        binding.comicInfoDescriptionAll.visibility = View.GONE
        isExtend = false
    }

    private fun extendText() {
        binding.comicInfoDescription.visibility = View.GONE
        binding.comicInfoDescriptionAll.visibility = View.VISIBLE
        isExtend = true
    }


}