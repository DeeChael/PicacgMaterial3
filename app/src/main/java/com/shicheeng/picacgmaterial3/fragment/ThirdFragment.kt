package com.shicheeng.picacgmaterial3.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.adapter.ComicItemCommonAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.ComicItemCommonData
import com.shicheeng.picacgmaterial3.databinding.FragmentThirdBinding
import com.shicheeng.picacgmaterial3.main.FavoriteComicActivity
import com.shicheeng.picacgmaterial3.viewmodel.MyViewModel
import com.shicheeng.picacgmaterial3.widget.setAdapterWithHorizontalLayout
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean
import kotlin.math.min

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ThirdFragment : Fragment() {

    companion object {
        fun newInstance(token: String): ThirdFragment {
            val args = Bundle()
            args.putString("TOKEN", token)
            val fragment = ThirdFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = arguments?.getString("TOKEN")!!
        var progressNum = 0

        viewModel.profile(token)
        viewModel.favoriteBooks(1, token)


        viewModel.profileUser.observe(viewLifecycleOwner) {

            val profileJson = JsonParser.parseString(it).asJsonObject["data"]
                .asJsonObject["user"].asJsonObject
            val name = profileJson["name"].asString
            val title = profileJson["title"].asString
            progressNum = profileJson["exp"].asInt
            val level = profileJson["level"].asInt.toString()
            val url = if (profileJson.keySet().contains("avatar")) {
                profileJson["avatar"].asJsonObject.let { avatarUrl ->
                    Utils.getComicRankImage(avatarUrl["fileServer"].asString,
                        avatarUrl["path"].asString)
                }
            } else {
                "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_960_720.png"
            }
            binding.headerName.text = name
            binding.headerTitleLevel.text = "等级 $level"
            binding.expToNextLevel.text = "$progressNum/200"
            binding.headerTitleName.text = title
            viewModel.headerImage(url)

        }

        viewModel.bitmapH.observe(viewLifecycleOwner) {
            val palette = Palette.from(it).generate()
            binding.headerImage.setImageBitmap(it)
            binding.headerLayout.setCardBackgroundColor(palette.getDominantColor(Color.WHITE))
            val color = palette.dominantSwatch?.bodyTextColor ?: Color.BLACK
            binding.apply {
                headerName.setTextColor(color)
                headerTitleLevel.setTextColor(color)
                headerTitleName.setTextColor(color)
                expToNextLevel.setTextColor(color)
            }
        }

        viewModel.favoriteUser.observe(viewLifecycleOwner) {
            val listItem = ArrayList<ComicItemCommonData>()
            val commonAdapter = ComicItemCommonAdapter(listItem)

            val favoriteJson = JsonParser.parseString(it)
                .asJsonObject["data"].asJsonObject["comics"]
                .asJsonObject["docs"].asJsonArray


            val max = min(favoriteJson.size(), 5)
            for (element in 0 until max) {
                val json = favoriteJson[element].asJsonObject
                val id = json["_id"].asString
                val title = json["title"].asString
                val author = json["author"].asString
                val url = json["thumb"].asJsonObject.let { jsonObject ->
                    Utils.getComicRankImage(jsonObject["fileServer"].asString,
                        jsonObject["path"].asString)
                }
                val item = ComicItemCommonData(id, title, author, url)
                listItem.add(item)
            }

            binding.myFavoriteRecycler.setAdapterWithHorizontalLayout(commonAdapter)

        }

        viewModel.loadProgressLin.observe(viewLifecycleOwner) {
            if (!it) {
                val num = (progressNum.toFloat() / 200) * 100
                binding.headerLevelIndication.isIndeterminate = false
                binding.headerLevelIndication.setProgressCompat(num.toInt(), true)
            }
        }

        viewModel.loadProgressCir.observe(viewLifecycleOwner) {
            binding.loadingFavIndication.setShowWithBoolean(it)
        }

        viewModel.favoriteUserError.observe(viewLifecycleOwner) {
            binding.myFavoriteRetryButton.apply {
                when (it) {
                    Utils.ERROR_TIME_OUT -> {
                        visibility = View.VISIBLE
                    }
                    Utils.ERROR_LOADING -> {
                        visibility = View.VISIBLE
                    }
                }
                setOnClickListener {
                    viewModel.favoriteBooks(1, token)
                    visibility = View.GONE
                    binding.loadingFavIndication.setShowWithBoolean(true)
                }
            }
        }

        viewModel.profileError.observe(viewLifecycleOwner) {
            binding.headerName.text = it
            binding.headerTitleLevel.text = getString(R.string.touch_to_retry)
            binding.headerLayout.setOnClickListener {
                viewModel.profile(token)
                binding.headerLevelIndication.isIndeterminate = true
            }
        }

        binding.inToAllMyFavoriteBook.setOnClickListener {
            val intent = Intent()
            intent.setClass(requireContext(), FavoriteComicActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}