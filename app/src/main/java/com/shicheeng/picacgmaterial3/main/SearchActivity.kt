package com.shicheeng.picacgmaterial3.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import com.google.gson.JsonObject
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.RankAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.RankData
import com.shicheeng.picacgmaterial3.databinding.ActivitySearchBinding
import com.shicheeng.picacgmaterial3.inapp.AppActivity
import com.shicheeng.picacgmaterial3.viewmodel.SearchViewModel
import com.shicheeng.picacgmaterial3.widget.setAdapterWithLinearLayout

class SearchActivity : AppActivity(), TextView.OnEditorActionListener {

    private lateinit var binding: ActivitySearchBinding
    private val viewM: SearchViewModel by viewModels()
    private var page: Int = 1
    private val mainList = ArrayList<RankData>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        paddingUp(viewRoot, binding.searchAppBar)
        val token = token()!!
        binding.searchKeywordList.setShowState(false)
        binding.searchCircularIndication.showState(true)
        viewM.onKeyWordList(token)
        viewM.keywordList.observe(this) {

            binding.searchCircularIndication.showState(false)
            binding.searchKeywordList.setShowState(true)
            binding.searchKeywordList.setChips(it) { s ->
                binding.searchToolbarEdit.setText(s)
                viewM.onSearch(s, token = token, page = page)
                binding.searchKeywordList.setShowState(false)
                binding.searchCircularIndication.showState(true)
            }
        }
        binding.searchToolbarEdit.setOnEditorActionListener(this)

        val adapterRank = RankAdapter(mainList)
        binding.searchResultList.setAdapterWithLinearLayout(adapterRank)
        viewM.searchResult.observe(this) {
            binding.searchResultList.visibility = View.VISIBLE
            val dataList = loadData(it)
            binding.searchCircularIndication.showState(false)
            mainList.addAll(dataList)
            adapterRank.notifyDataSetChanged()
        }

        binding.searchToolBar.setNavigationOnClickListener { finish() }

    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {

        return when (p1) {
            EditorInfo.IME_ACTION_SEARCH -> {
                if (p0?.text.isNullOrBlank()) {
                    false
                } else {
                    val s = p0?.text.toString()
                    //隐藏软键盘
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
                    mainList.clear()
                    viewM.onSearch(s, page = page, token = token()!!)
                    binding.searchCircularIndication.showState(true)
                    binding.searchKeywordList.setShowState(false)
                    true
                }
            }

            else -> false
        }
    }


    private fun loadData(jsonObject: JsonObject): ArrayList<RankData> {
        val arrayList = ArrayList<RankData>()
        val jsonArrayList =
            jsonObject["data"].asJsonObject["comics"].asJsonObject["docs"].asJsonArray
        jsonArrayList.forEach {
            val jsonBlock = it.asJsonObject
            val cateList = ArrayList<String>()
            val thumbUrl = jsonBlock["thumb"].asJsonObject.let { thumbBlock ->
                Utils.getComicRankImage(thumbBlock["fileServer"].asString,
                    thumbBlock["path"].asString)
            }
            val author =
                if (jsonBlock.keySet().contains("author")) jsonBlock["author"].asString
                else getString(R.string.unknown_author)
            jsonBlock["categories"].asJsonArray.toList().forEach { s ->
                cateList.add(s.asString)
            }
            val totalLikes =
                if (jsonBlock.keySet().contains("totalLikes")) jsonBlock["totalLikes"].asInt
                else 0
            val title = jsonBlock["title"].asString
            val id = jsonBlock["_id"].asString
            val rankData = RankData(id,
                title,
                author,
                thumbUrl,
                cateList,
                totalLikes,
                getString(R.string.like_count))
            arrayList.add(rankData)

        }
        return arrayList
    }

}