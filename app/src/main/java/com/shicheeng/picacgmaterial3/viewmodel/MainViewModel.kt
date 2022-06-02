package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class MainViewModel : ViewModel() {

    private val _dataCategory: MutableLiveData<String> = MutableLiveData()
    val dataCategory: LiveData<String> = _dataCategory

    private val _dataToken: MutableLiveData<String> = MutableLiveData()
    val dataToken: LiveData<String> = _dataToken

    private val _indication: MutableLiveData<Boolean> = MutableLiveData()
    val indication: LiveData<Boolean> = _indication

    private val _dataTokenError: MutableLiveData<String> = MutableLiveData()
    val dataTokenError: LiveData<String> = _dataTokenError

    private val _categoryError: MutableLiveData<String> = MutableLiveData()
    val categoryError: LiveData<String> = _categoryError

    private val utils = Utils()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {

                    val tokenJson = utils.getToken(username, password)
                    val token = JsonParser.parseString(tokenJson)
                        .asJsonObject["data"].asJsonObject["token"].asString
                    _dataToken.postValue(token)

                } catch (e: SocketTimeoutException) {
                    _dataTokenError.postValue("登录超时")
                } catch (e: Exception) {
                    _dataTokenError.postValue("登录错误")
                }
            }
        }
    }

    fun category(token: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _dataCategory.postValue(utils.getComicsCategory(token))
                } catch (e: SocketTimeoutException) {
                    _categoryError.postValue("超时")
                } catch (e: Exception) {
                    _categoryError.postValue("错误")
                }
                _indication.postValue(false)
            }
        }
    }

    fun indicationShow(show: Boolean) {
        _indication.postValue(show)
    }

}