package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class ComicReaderViewModel : ViewModel() {

    private val _comicPictureData: MutableLiveData<String> = MutableLiveData()
    val comicPictureData: LiveData<String> = _comicPictureData

    private val _comicPictureUrl: MutableLiveData<MutableList<String>> = MutableLiveData()
    val comicPictureUrl: LiveData<MutableList<String>> = _comicPictureUrl

    private val _comicLoadShow: MutableLiveData<Boolean> = MutableLiveData()
    val comicLoadShow: LiveData<Boolean> = _comicLoadShow

    private val _errorParserUrl: MutableLiveData<Int> = MutableLiveData()
    val errorParserUrl: LiveData<Int> = _errorParserUrl

    private val _errorDataGet: MutableLiveData<Int> = MutableLiveData()
    val errorDateGet: LiveData<Int> = _errorDataGet

    fun loadComicUri(comicId: String, order: Int, page: Int, token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val comicUris = Utils().getComicPicture(comicId, order, page, token)
                    _comicPictureData.postValue(comicUris)

                } catch (e: SocketTimeoutException) {
                    _errorDataGet.postValue(R.string.loading_data_chao_shi)
                } catch (e: Exception) {
                    _errorDataGet.postValue(R.string.loading_data_shi_ba)
                }
            }

        }
    }

    /**
     * 加载除所有的图片链接
     */
    fun loadComicsUrls(comicId: String, order: Int, token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val page = 1
                    val comicUris = Utils().getComicPicture(comicId, order, page, token)
                    val jsonA = JsonParser.parseString(comicUris).asJsonObject
                    val jsonAA = jsonA["data"].asJsonObject["pages"].asJsonObject
                    val pageLimit = jsonAA["pages"].asInt
                    val arrayList = ArrayList<String>()

                    jsonAA["docs"].asJsonArray.forEach {
                        val url = it.asJsonObject["media"].asJsonObject.let { media ->
                            Utils.getComicRankImage(media["fileServer"].asString,
                                media["path"].asString)
                        }
                        arrayList.add(url)
                    }

                    for (i in (page + 1) until (pageLimit + 1)) {
                        val nextComicUris = Utils().getComicPicture(comicId, order, i, token)
                        val jsonB = JsonParser.parseString(nextComicUris).asJsonObject
                        val jsonBB = jsonB["data"].asJsonObject["pages"].asJsonObject
                        jsonBB["docs"].asJsonArray.forEach {

                            val url = it.asJsonObject["media"].asJsonObject.let { media ->
                                Utils.getComicRankImage(media["fileServer"].asString,
                                    media["path"].asString)
                            }
                            arrayList.add(url)
                        }
                    }
                    _comicPictureUrl.postValue(arrayList)
                } catch (e: SocketTimeoutException) {
                    _errorParserUrl.postValue(R.string.parser_uri_error_time)
                    e.printStackTrace()
                } catch (e: Exception) {
                    _errorParserUrl.postValue(R.string.parser_uri_error)
                    e.printStackTrace()
                }
                _comicLoadShow.postValue(false)
            }

        }
    }
}