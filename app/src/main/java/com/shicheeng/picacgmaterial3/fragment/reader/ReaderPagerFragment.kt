package com.shicheeng.picacgmaterial3.fragment.reader

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shicheeng.picacgmaterial3.databinding.FragmentImageBinding
import com.shicheeng.picacgmaterial3.viewmodel.ComicReaderPagerViewModel

class ReaderPagerFragment : Fragment() {

    companion object {
        fun newInstance(url: String): ReaderPagerFragment {
            val args = Bundle()
            args.putString("URL", url)
            val fragment = ReaderPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ComicReaderPagerViewModel by viewModels()
    private var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentImageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lianJie = arguments?.getString("URL")!!
        viewModel.loadingBitmap(lianJie)
        viewModel.imageBitmap.observe(viewLifecycleOwner) {
            bitmap = it
            binding.comicReaderImage.setImageBitmap(bitmap)
        }
        viewModel.showState.observe(viewLifecycleOwner) {
            binding.comicImageIndicator.showState(it)
        }
        viewModel.errorLoad.observe(viewLifecycleOwner) {
            binding.comicImageButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    viewModel.loadingBitmap(lianJie)
                    binding.comicImageIndicator.showState(true)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        bitmap?.recycle()
        bitmap = null
    }

}