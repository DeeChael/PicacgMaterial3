package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class FavoriteViewModel : ViewModel() {

    private val _favoriteData: MutableLiveData<String> = MutableLiveData()
    val favoriteData: LiveData<String> = _favoriteData

    private val _loadProgressCir: MutableLiveData<Boolean> = MutableLiveData()
    val loadProgressCir: LiveData<Boolean> = _loadProgressCir

    private val _loadSee: MutableLiveData<String> = MutableLiveData()
    val loadSee: LiveData<String> = _loadSee

    private val _bottomBar: MutableLiveData<Boolean> = MutableLiveData()
    val bottomBar: LiveData<Boolean> = _bottomBar

    fun favoriteBooks(pages: Int, token: String) {

        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val fav = Utils().getMyFavoriteBooks(pages, token)
                    _favoriteData.postValue(fav)
                } catch (e: SocketTimeoutException) {
                    _loadSee.postValue("超时")
                } catch (e: Exception) {
                    _loadSee.postValue("加载错误")
                }
                _bottomBar.postValue(false)
                _loadProgressCir.postValue(false)

            }
        }
    }

}