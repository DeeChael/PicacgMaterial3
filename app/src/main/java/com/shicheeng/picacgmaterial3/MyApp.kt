package com.shicheeng.picacgmaterial3

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var contextBase: Context
    }

    override fun onCreate() {
        super.onCreate()
        contextBase = baseContext
    }

}