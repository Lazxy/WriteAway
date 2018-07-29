package com.work.lazxy.writeaway.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Message

import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.ConfigManager
import com.work.lazxy.writeaway.common.Constant
import com.work.lazxy.writeaway.db.NoteDataHandler
import com.work.lazxy.writeaway.event.EventChangeNote
import com.work.lazxy.writeaway.event.EventImportComplete
import com.work.lazxy.writeaway.ui.activity.MainActivity
import com.work.lazxy.writeaway.utils.FileUtils
import com.work.lazxy.writeaway.utils.StringUtils
import com.work.lazxy.writeaway.utils.ZipUtils
import org.greenrobot.eventbus.EventBus

import java.io.File

/**
 * Created by Lazxy on 2018/7/22.
 */
class ImportNoteService : BaseForegroundService<EventImportComplete>() {
    private val IMPORT_NOTIFICATION_ID = 0x20

    private lateinit var mFilePaths: MutableList<String>

    override fun onCreate() {
        mBuilder = Notification.Builder(this.applicationContext)
        mBuilder.setContentTitle("正在准备文件")
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setProgress(100, 0, true)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mFilePaths = intent.getStringArrayListExtra(Constant.Extra.EXTRA_IMPORT_PATH)
        /*这里开始进行导入文件的异步操作*/
        if (mTaskRunnable != null && mFilePaths.isNotEmpty()) {
            startForeground(IMPORT_NOTIFICATION_ID, mBuilder.build())
            Thread(mTaskRunnable).start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    override fun onActionDone(event: EventImportComplete) {
        if (event.mIsSuccessful) {
            stopForeground(true)
            showEndNotification("导入完成")
            stopSelf()
        } else {
            stopForeground(true)
            showEndNotification(event.mErrorMessage)
            stopSelf()
        }
        EventBus.getDefault().post(EventChangeNote(true, null))
    }

    private fun showEndNotification(msg: String) {
        val notification = Notification.Builder(this.applicationContext)
                .setContentTitle("StNote")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setContentIntent(PendingIntent.getActivity(this, REQUEST_TO_APPLICATION, Intent(this, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT))
                .build()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(IMPORT_NOTIFICATION_ID, notification)
    }

    private val mTaskRunnable = Runnable {
        var isAllComplete = true
        mFilePaths.forEach { path ->
            if (path.endsWith(FileUtils.TYPE_TEXT)) {
                if (!saveFileToNote(path)) {
                    isAllComplete = false
                }
            } else if (path.endsWith(FileUtils.TYPE_ZIP)) {
                val zipFile = File(path)
                if (zipFile.exists()) {
                    val paths = ZipUtils.upZipFile(zipFile, FileUtils.DEFAULT_TEMP_FOLDER)
                    if (paths.size > 0) {
                        paths.forEach {
                            if (!saveFileToNote(it)) {
                                isAllComplete = false
                            } else {
                                File(it).delete()
                            }
                        }
                    }
                }
            }
        }
        val msg = mActionDoneHandler.obtainMessage()
        if (isAllComplete)
            msg.obj = EventImportComplete(0, 1, true)
        else
            msg.obj = EventImportComplete(false, "导入完成，但中间可能出了点小问题")
        mActionDoneHandler.sendMessage(msg)
    }

    private fun saveFileToNote(path: String): Boolean {
        if (path.endsWith(FileUtils.TYPE_TEXT)) {
            //直接解析文本文件，将其存入数据库
            if (File(path).exists()) {
                val content = FileUtils.readTextFormFile(path)
                val title = path.split("/").last().replace(FileUtils.TYPE_TEXT, "")
                val newPath = FileUtils.createFileWithTime(ConfigManager.getFileSavedPath())
                NoteDataHandler.getInstance().saveData(title, newPath,
                        StringUtils.getPreview(content), System.currentTimeMillis())
                if (content != null) {
                    return FileUtils.writeTextToFile(newPath, content)
                }
            }
        }
        return true
    }
}
