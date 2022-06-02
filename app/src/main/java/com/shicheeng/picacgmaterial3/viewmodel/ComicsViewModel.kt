package com.shicheeng.picacgmaterial3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicsViewModel : ViewModel() {

    private val _categoryData: MutableLiveData<String> = MutableLiveData()
    val categoryData: LiveData<String> = _categoryData

    private val _indication: MutableLiveData<Boolean> = MutableLiveData()
    val indication: LiveData<Boolean> = _indication

    private val _commonLoadingBar: MutableLiveData<Boolean> = MutableLiveData()
    val commonLoadingBar: LiveData<Boolean> = _commonLoadingBar


    fun comics(page: Int, c: String, order: String, token: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                val data = Utils().getComics(page.toString(), c, order, token)
                _categoryData.postValue(data)
                _indication.postValue(false)
                _commonLoadingBar.postValue(false)
            }
        }
    }


    fun commonBarShow(boolean: Boolean) {
        _commonLoadingBar.postValue(boolean)
    }

}