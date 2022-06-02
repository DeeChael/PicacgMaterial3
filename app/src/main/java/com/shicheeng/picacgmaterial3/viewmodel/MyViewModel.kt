package com.shicheeng.picacgmaterial3.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

@Suppress("BlockingMethodInNonBlockingContext")
class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val _profileUser: MutableLiveData<String> = MutableLiveData()
    val profileUser: LiveData<String> = _profileUser

    private val _favoriteUser: MutableLiveData<String> = MutableLiveData()
    val favoriteUser: LiveData<String> = _favoriteUser

    private val _bitmapH: MutableLiveData<Bitmap> = MutableLiveData()
    val bitmapH: LiveData<Bitmap> = _bitmapH

    private val _loadProgressLin: MutableLiveData<Boolean> = MutableLiveData()
    val loadProgressLin: LiveData<Boolean> = _loadProgressLin

    private val _loadProgressCir: MutableLiveData<Boolean> = MutableLiveData()
    val loadProgressCir: LiveData<Boolean> = _loadProgressCir

    private val _favoriteUserError: MutableLiveData<Int> = MutableLiveData()
    private val _profileError: MutableLiveData<String> = MutableLiveData()

    val favoriteUserError: LiveData<Int> = _favoriteUserError
    val profileError: LiveData<String> = _profileError


    fun profile(token: String) {

        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val profileString = Utils().getUserProfile(token)
                    _profileUser.postValue(profileString)
                } catch (e: SocketTimeoutException) {
                    _profileError.postValue("获取超时")
                } catch (e: Exception) {
                    _profileError.postValue("错误")
                }
            }
        }
    }

    fun headerImage(url: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap =
                    Glide.with(Glide.get(getApplication()).context).asBitmap().load(url).submit()
                        .get()
                _bitmapH.postValue(bitmap)
                _loadProgressLin.postValue(false)
            }
        }
    }

    fun favoriteBooks(pages: Int, token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val fav = Utils().getMyFavoriteBooks(pages, token)
                    _favoriteUser.postValue(fav)

                } catch (e: SocketTimeoutException) {
                    _favoriteUserError.postValue(Utils.ERROR_TIME_OUT)
                } catch (e: Exception) {
                    _favoriteUserError.postValue(Utils.ERROR_LOADING)
                }
                _loadProgressCir.postValue(false)
            }
        }
    }


}