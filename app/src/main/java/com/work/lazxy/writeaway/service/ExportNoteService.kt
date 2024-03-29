package com.work.lazxy.writeaway.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.TipConstant.WarningTips.MIGRATE_FILE_LOSE
import com.work.lazxy.writeaway.common.TipConstant.WarningTips.MIGRATE_NOTE_NULL
import com.work.lazxy.writeaway.common.TipConstant.WarningTips.MIGRATE_ZIP_FAILED
import com.work.lazxy.writeaway.db.NoteDataHandler
import com.work.lazxy.writeaway.event.EventCompressComplete
import com.work.lazxy.writeaway.service.DataMigrateHelper.clearMigrateCache
import com.work.lazxy.writeaway.service.DataMigrateHelper.renameFileWithCreateTime
import com.work.lazxy.writeaway.ui.activity.MainActivity
import com.work.lazxy.writeaway.utils.FileProviderUtil
import com.work.lazxy.writeaway.utils.FileUtils
import com.work.lazxy.writeaway.utils.ZipUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

/**
 * Created by Lazxy on 2017/5/30.
 */
class ExportNoteService : BaseForegroundService<EventCompressComplete?>() {
    private val EXPORT_NOTIFICATION_ID = 0x10

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        mBuilder = createChannelBuilder()
        mBuilder.setContentTitle("正在准备文件")
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setProgress(100, 0, true)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(EXPORT_NOTIFICATION_ID, mBuilder.build())
        /*这里开始进行压缩文件的异步操作*/Thread(mRunnable).start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        stopForeground(true)
        super.onDestroy()
    }

    override fun onActionDone(event: EventCompressComplete?) {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCompressComplete(event: EventCompressComplete) {
        if (event.mIsSuccessful) {
            val index = event.mCurrentIndex + 1
            if (index == 1) {
                mBuilder.setContentTitle("导出中")
            }
            mBuilder.setProgress(100, index * 100 / event.mTaskSum, false)
            mBuilder.setContentText(index.toString() + " / " + event.mTaskSum)
            startForeground(EXPORT_NOTIFICATION_ID, mBuilder.build())
            if (index == event.mTaskSum) {
                stopForeground(true)
                showEndNotification("导出完成")
                stopSelf()
                Toast.makeText(baseContext, "导出完成！", Toast.LENGTH_SHORT).show()
            }
        } else {
            stopForeground(true)
            showEndNotification(event.mErrorMessage)
            stopSelf()
            Toast.makeText(baseContext, "导出失败！", Toast.LENGTH_SHORT).show()
        }
    }

    private val mRunnable = Runnable {
        val notes = NoteDataHandler.instance.getAllData()
        if (notes.isNotEmpty()) {
            val outputFiles = renameFileWithCreateTime(notes)
            val sum = outputFiles.size
            if (sum == 0) {
                EventBus.getDefault().post(EventCompressComplete(false, MIGRATE_NOTE_NULL))
                return@Runnable
            }
            try {
                val zipFile = FileUtils.createDefaultCompressFile();
                ZipUtils.zipFiles(outputFiles, zipFile) {
                    progress, file ->
                    EventBus.getDefault().post(EventCompressComplete(progress, sum, true))
                }
                FileProviderUtil.moveFileToDownloads(baseContext,zipFile)
            } catch (e: IOException) {
                e.printStackTrace()
                EventBus.getDefault().post(EventCompressComplete(false, MIGRATE_ZIP_FAILED))
                return@Runnable
            } finally {
                clearMigrateCache()
            }
        } else {
            EventBus.getDefault().post(EventCompressComplete(false, MIGRATE_FILE_LOSE))
        }
    }

    private fun showEndNotification(msg: String?) {
        val builder = createChannelBuilder()
        val notification = builder.setContentTitle("StNote")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setContentIntent(PendingIntent.getActivity(this, REQUEST_TO_APPLICATION, Intent(this, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true)
                .build()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(EXPORT_NOTIFICATION_ID, notification)
    }
}