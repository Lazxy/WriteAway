package com.work.lazxy.writeaway

import android.app.Application
import android.content.Context

/**
 * Created by Lazxy on 2017/4/27.
 */
class WriteAway : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        lateinit var appContext: Context;
    }
}