package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.picacgmaterial3.api.LoginFException
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class RankViewModel : ViewModel() {

    private val _rankData: MutableLiveData<String> = MutableLiveData()
    val rankData: LiveData<String> = _rankData

    private val _indication: MutableLiveData<Boolean> = MutableLiveData()
    val indicator: LiveData<Boolean> = _indication

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    fun rank(url: String, token: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _rankData.postValue(Utils().getRankComic(url, token))
                } catch (e: SocketTimeoutException) {
                    _errorMessage.postValue("获取超时")
                } catch (e: Exception) {
                    _errorMessage.postValue("获取错误")
                } catch (e: LoginFException) {
                    _errorMessage.postValue("登录过期")
                }
                _indication.postValue(false)
            }
        }
    }

}