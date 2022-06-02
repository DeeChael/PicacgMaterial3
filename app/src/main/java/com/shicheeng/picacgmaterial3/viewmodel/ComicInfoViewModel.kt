package com.shicheeng.picacgmaterial3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class ComicInfoViewModel : ViewModel() {

    private val _comicInfoData: MutableLiveData<String> = MutableLiveData()
    val comicInfoData: LiveData<String> = _comicInfoData

    private val _comicChapterData: MutableLiveData<String> = MutableLiveData()
    val comicChapterData: LiveData<String> = _comicChapterData

    private val _comicInfoError: MutableLiveData<Int> = MutableLiveData()
    val comicInfoError: LiveData<Int> = _comicInfoError

    private val _show: MutableLiveData<Boolean> = MutableLiveData()
    val show: LiveData<Boolean> = _show

    private val _chapterShow: MutableLiveData<Boolean> = MutableLiveData()
    val chapterShow: LiveData<Boolean> = _chapterShow

    fun getComicInfoData(comicId: String, token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val comicInfo = Utils().getComicInfo(comicId, token)
                    _comicInfoData.postValue(comicInfo)

                } catch (e: SocketTimeoutException) {

                    _comicInfoError.postValue(Utils.ERROR_TIME_OUT)
                    return@withContext

                } catch (e: Exception) {

                    _comicInfoError.postValue(Utils.ERROR_LOADING)
                    return@withContext

                }
                _show.postValue(false)
            }
        }
    }

    fun getComicChapterData(comicId: String, page: Int, token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                val comicChapter = Utils().getComicEps(comicId, page, token)
                _comicChapterData.postValue(comicChapter)
                _chapterShow.postValue(false)
            }
        }
    }

}