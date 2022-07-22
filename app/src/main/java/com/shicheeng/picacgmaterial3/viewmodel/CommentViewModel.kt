package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class CommentViewModel : ViewModel() {

    private val _data: MutableLiveData<JsonArray> = MutableLiveData()
    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    private val _showState: MutableLiveData<Boolean> = MutableLiveData()
    private val _pages: MutableLiveData<Int> = MutableLiveData()

    val data: LiveData<JsonArray> = _data
    val errorMessage: LiveData<String> = _errorMessage
    val showState: LiveData<Boolean> = _showState
    val pages: LiveData<Int> = _pages

    fun loadData(comicId: String, page: Int, token: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val json = Utils().getComicComments(comicId, page, token)
                    val jsonArray =
                        JsonParser.parseString(json)
                            .asJsonObject["data"]
                            .asJsonObject["comments"]
                            .asJsonObject["docs"]
                            .asJsonArray
                    val pages = JsonParser.parseString(json)
                        .asJsonObject["data"]
                        .asJsonObject["comments"].asJsonObject["pages"].asInt
                    _data.postValue(jsonArray)
                    _pages.postValue(pages)
                } catch (e: SocketTimeoutException) {
                    _errorMessage.postValue("超时")
                }
                _showState.postValue(false)
            }
        }
    }

}