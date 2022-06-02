package com.shicheeng.picacgmaterial3.fragment.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.adapter.RankAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.RankData
import com.shicheeng.picacgmaterial3.databinding.FragmentRankBinding
import com.shicheeng.picacgmaterial3.viewmodel.RankViewModel
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean

class RankFragment : Fragment() {

    companion object {

        fun newInstance(url: String, token: String): RankFragment {
            val args = Bundle()
            args.putString("KEY_URL", url)
            args.putString("TOKEN", token)
            val fragment = RankFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RankViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRankBinding.inflate(inflater, container, false)
        binding.rankFragmentModernButton.showState(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString("KEY_URL")!!
        val token = arguments?.getString("TOKEN")!!
        viewModel.rank(url, token)
        viewModel.rankData.observe(viewLifecycleOwner) {
            val listRank = ArrayList<RankData>()
            val json = JsonParser.parseString(it).asJsonObject["data"]
                .asJsonObject["comics"].asJsonArray
            for (jsonElement in json) {
                val comicsJson = jsonElement.asJsonObject
                val id = comicsJson["_id"].asString
                val title = comicsJson["title"].asString
                val author = comicsJson["author"].asString
                val leaderboardCount = comicsJson["leaderboardCount"].asInt
                val path = comicsJson["thumb"].asJsonObject["path"].asString
                val fileServer = comicsJson["thumb"].asJsonObject["fileServer"].asString
                val urlPath = Utils.getComicRankImage(fileServer, path)
                val listCate = comicsJson["categories"].asJsonArray
                val list = ArrayList<String>()
                for (element in listCate) {
                    list.add(element.asString)
                }
                val rankData = RankData(id, title, author, urlPath, list, leaderboardCount)
                listRank.add(rankData)
            }
            val vLayout = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            val rAdapter = RankAdapter(listRank)
            binding.rankFragmentRecycler.apply {
                adapter = rAdapter
                layoutManager = vLayout
            }

        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            binding.rankFragmentModernButton.apply {
                binding.rankFragmentModernButton.apply {
                    showState(true)
                    setTipText(it)
                    setOnClickListener {
                        viewModel.rank(url, token)
                        showState(false)
                        binding.rankFragmentCircularProgress.setShowWithBoolean(true)
                    }
                }
            }
        }

        viewModel.indicator.observe(viewLifecycleOwner) {
            binding.rankFragmentCircularProgress.setShowWithBoolean(it)
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { viewIn: View, windowInsetsCompat: WindowInsetsCompat ->

            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            viewIn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                rightMargin = insets.right
                leftMargin = insets.left
            }

            binding.rankFragmentRecycler.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}