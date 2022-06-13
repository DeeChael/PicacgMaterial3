package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class SearchViewModel : ViewModel() {

    private val _keywordList: MutableLiveData<List<String>> = MutableLiveData()
    private val _keywordListError: MutableLiveData<Int> = MutableLiveData()
    private val _searchResult: MutableLiveData<JsonObject> = MutableLiveData()
    private val _searchResultError: MutableLiveData<Int> = MutableLiveData()

    val keywordList: LiveData<List<String>> = _keywordList
    val keywordListError: LiveData<Int> = _keywordListError
    val searchResult: LiveData<JsonObject> = _searchResult
    val searchResultError: LiveData<Int> = _searchResultError


    fun onKeyWordList(token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val s = Utils().getComicKeyWord(token).let {
                        JsonParser.parseString(it).asJsonObject
                    }["data"].asJsonObject["keywords"].asJsonArray
                    val list = ArrayList<String>()
                    s.forEach {
                        list.add(it.asString)
                    }
                    _keywordList.postValue(list)
                } catch (e: SocketTimeoutException) {
                    _keywordListError.postValue(R.string.loading_data_chao_shi)
                } catch (e: Exception) {
                    _keywordListError.postValue(R.string.loading_data_shi_ba)
                }


            }

        }
    }

    fun onSearch(
        t: String,
        category: String? = null,
        sort: String? = null,
        token: String,
        page: Int,
    ) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val s = Utils().searchComic(category, t, sort, page, token).let {
                        JsonParser.parseString(it).asJsonObject
                    }
                    _searchResult.postValue(s)
                } catch (e: SocketTimeoutException) {
                    _searchResultError.postValue(R.string.loading_data_chao_shi)
                } catch (e: Exception) {
                    _searchResultError.postValue(R.string.loading_data_shi_ba)
                }

            }

        }
    }

}