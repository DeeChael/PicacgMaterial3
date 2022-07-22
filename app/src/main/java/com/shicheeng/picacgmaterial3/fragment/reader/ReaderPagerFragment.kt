package com.shicheeng.picacgmaterial3.fragment.reader

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.davemorrissey.labs.subscaleview.ImageSource
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
    private lateinit var onClick: (v: View) -> Unit
    private val viewModel: ComicReaderPagerViewModel by viewModels()
    private var _bitmap: Bitmap? = null
    private val bitmap get() = _bitmap!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentImageBinding.inflate(layoutInflater, container, false)
        binding.comicReaderImage.setOnClickListener {
            onClick.invoke(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lianJie = arguments?.getString("URL")!!
        viewModel.loadingBitmap(lianJie)

        viewModel.imageBitmap.observe(viewLifecycleOwner) {
            _bitmap = it
            binding.comicReaderImage.setImage(ImageSource.bitmap(bitmap))
        }

        viewModel.showState.observe(viewLifecycleOwner) {
            binding.comicImageIndicator.showState(it)
        }

        viewModel.errorLoad.observe(viewLifecycleOwner) {
            binding.comicImageButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    viewModel.loadingBitmap(lianJie)
                    visibility = View.VISIBLE
                    binding.comicImageIndicator.showState(true)
                }
            }
        }
    }

    fun setOnFragmentImageClick(onClick: (v: View) -> Unit) {
        this.onClick = onClick
    }

    override fun onDestroy() {
        super.onDestroy()
        _bitmap?.recycle()
        _bitmap = null
        _binding = null
    }

}