package com.shicheeng.picacgmaterial3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.shicheeng.picacgmaterial3.adapter.pagers.PagerTabAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.databinding.FragmentSecondBinding
import com.shicheeng.picacgmaterial3.fragment.rank.RankFragment


class SecondFragment : Fragment() {

    companion object {

        fun newInstance(token: String): SecondFragment {
            val args = Bundle()
            args.putString("TOKEN", token)
            val fragment = SecondFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = arguments?.getString("TOKEN")!!
        val tabL = binding.secondTabLayout
        val pagerSecond = binding.secondViewPager2
        val tabList = listOf(
            RankFragment.newInstance(Utils().urlOnRank[Utils.RankKey.H24]!!, token),
            RankFragment.newInstance(Utils().urlOnRank[Utils.RankKey.D7]!!, token),
            RankFragment.newInstance(Utils().urlOnRank[Utils.RankKey.D30]!!, token)
        )

        val tabText = listOf(
            "日榜",
            "周榜",
            "月榜"
        )

        val tabAdapter = PagerTabAdapter(tabList, childFragmentManager, this.lifecycle)
        pagerSecond.apply {
            adapter = tabAdapter
            offscreenPageLimit = 1
        }

        TabLayoutMediator(tabL, pagerSecond) { tab: TabLayout.Tab, position: Int ->
            tab.text = tabText[position]
        }.attach()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}