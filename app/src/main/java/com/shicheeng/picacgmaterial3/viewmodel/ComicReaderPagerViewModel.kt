package com.shicheeng.picacgmaterial3.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.picacgmaterial3.api.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.ExecutionException

@Suppress("BlockingMethodInNonBlockingContext")
class ComicReaderPagerViewModel : ViewModel() {

    private val _imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val imageBitmap: LiveData<Bitmap> = _imageBitmap

    private val _showState: MutableLiveData<Boolean> = MutableLiveData()
    val showState: LiveData<Boolean> = _showState

    private val _errorLoad: MutableLiveData<Int> = MutableLiveData()
    val errorLoad: LiveData<Int> = _errorLoad

    fun loadingBitmap(url: String) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val op: BitmapFactory.Options = BitmapFactory.Options()
                    op.inSampleSize = 2
                    op.inJustDecodeBounds = false
                    val bitmapArray = Utils().getImageBitmap(url)
                    val bitmap =
                        BitmapFactory.decodeStream(bitmapArray, null, op)
                    _imageBitmap.postValue(bitmap)
                    bitmapArray.close()

                } catch (e: SocketException) {
                    _errorLoad.postValue(Utils.ERROR_LOADING)
                } catch (e: ExecutionException) {
                    _errorLoad.postValue(Utils.ERROR_LOADING)
                } catch (e: SocketTimeoutException) {
                    _errorLoad.postValue(Utils.ERROR_TIME_OUT)
                } catch (e: Exception) {
                    _errorLoad.postValue(Utils.ERROR_LOADING)
                }
                _showState.postValue(false)
            }
        }
    }
}