package com.shicheeng.picacgmaterial3.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.adapter.CategoryAdapter
import com.shicheeng.picacgmaterial3.api.Utils
import com.shicheeng.picacgmaterial3.data.CategoryData
import com.shicheeng.picacgmaterial3.databinding.FragmentFirstBinding
import com.shicheeng.picacgmaterial3.viewmodel.MainViewModel
import com.shicheeng.picacgmaterial3.widget.setShowWithBoolean

class FirstFragment : Fragment() {

    companion object {
        fun newInstance(token: String): FirstFragment {
            val args = Bundle()
            args.putString("TOKEN", token)
            val fragment = FirstFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.firstFModernButton.showState(false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = arguments?.getString("TOKEN")!!

        viewModel.category(token)

        viewModel.dataCategory.observe(viewLifecycleOwner) {
            val managerGrid = FlexboxLayoutManager(this.context)
            managerGrid.justifyContent = JustifyContent.SPACE_AROUND
            val json = JsonParser.parseString(it).asJsonObject
                .getAsJsonObject("data")
                .getAsJsonArray("categories")
            val list = ArrayList<CategoryData>()
            val cAdapter = CategoryAdapter(list, token)
            for (element in json) {
                val json2 = element.asJsonObject
                if (!json2.keySet().contains("isWeb")) {
                    val title = json2["title"].asString
                    val fileServer = json2["thumb"].asJsonObject["fileServer"].asString
                    val path = json2["thumb"].asJsonObject["path"].asString
                    val url = Utils().getComicCategoryImage(fileServer, path)
                    val data = CategoryData(title, url)
                    list.add(data)
                }
            }

            binding.firstFRecycler.apply {
                adapter = cAdapter
                layoutManager = managerGrid
            }
        }
        viewModel.indication.observe(viewLifecycleOwner) {
            binding.firstFCircularProgress.setShowWithBoolean(it)
        }

        viewModel.categoryError.observe(viewLifecycleOwner) {
            binding.firstFModernButton.apply {
                showState(true)
                setTipText(it)
                setOnClickListener {
                    viewModel.category(token)
                    binding.firstFCircularProgress.setShowWithBoolean(true)
                    showState(false)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { _: View, windowInsetsCompat: WindowInsetsCompat ->

            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.firstFRecycler.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}