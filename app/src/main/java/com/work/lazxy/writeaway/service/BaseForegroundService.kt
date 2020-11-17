package com.work.lazxy.writeaway.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message

/**
 * Created by Lazxy on 2018/7/22.
 */
abstract class BaseForegroundService<T> : Service() {
    protected lateinit var mBuilder: Notification.Builder
    private val FOREGROUND_CHANNEL_NAME = "前台服务"
    private val FOREGROUND_CHANNEL_ID = "foregroundChannelID"
    protected val REQUEST_TO_APPLICATION = 0x11

    protected var mActionDoneHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            onActionDone(msg.obj as T)
        }
    }

    override fun onCreate() {
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        mActionDoneHandler.removeCallbacksAndMessages(null)
    }

    protected abstract fun onActionDone(t: T)

    protected fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, FOREGROUND_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    protected fun createChannelBuilder(): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, FOREGROUND_CHANNEL_ID)
        } else {
            Notification.Builder(applicationContext)
        }
    }
}