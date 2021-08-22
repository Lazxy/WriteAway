package com.work.lazxy.writeaway.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast

import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.common.ConfigManager
import com.work.lazxy.writeaway.common.Constant
import com.work.lazxy.writeaway.db.NoteDataHandler
import com.work.lazxy.writeaway.event.EventChangeNote
import com.work.lazxy.writeaway.event.EventImportComplete
import com.work.lazxy.writeaway.ui.activity.MainActivity
import com.work.lazxy.writeaway.utils.FileProviderUtil
import com.work.lazxy.writeaway.utils.FileUtils
import com.work.lazxy.writeaway.utils.StringUtils
import com.work.lazxy.writeaway.utils.ZipUtils
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader

import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.jvm.Throws

/**
 * Created by Lazxy on 2018/7/22.
 */
class ImportNoteService : BaseForegroundService<EventImportComplete>() {
    private val IMPORT_NOTIFICATION_ID = 0x20

    private lateinit var mFileUris: ArrayList<Uri>

    override fun onCreate() {
        super.onCreate()
        mBuilder = createChannelBuilder()
        mBuilder.setContentTitle("正在准备文件")
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setProgress(100, 0, true)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mFileUris = intent.getParcelableArrayListExtra(Constant.Extra.EXTRA_IMPORT_PATH)!!
        /*这里开始进行导入文件的异步操作*/
        if (mFileUris.isNotEmpty()) {
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
            Toast.makeText(baseContext, "导入完成！", Toast.LENGTH_SHORT).show()
        } else {
            stopForeground(true)
            showEndNotification(event.mErrorMessage)
            stopSelf()
            Toast.makeText(baseContext, "导入失败！", Toast.LENGTH_SHORT).show()
        }
        EventBus.getDefault().post(EventChangeNote(true, null))
    }

    private fun showEndNotification(msg: String) {
        val builder = createChannelBuilder()
        val notification = builder.setContentTitle("StNote")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setContentIntent(PendingIntent.getActivity(this, REQUEST_TO_APPLICATION, Intent(this, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true)
                .build()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(IMPORT_NOTIFICATION_ID, notification)
    }

    private val mTaskRunnable = Runnable {
        var isAllComplete = true
        mFileUris.forEach { uri ->
            if (FileProviderUtil.MIME_TEXT == contentResolver.getType(uri)) {
                if (!saveFileToNote(uri)) {
                    isAllComplete = false
                }
            } else if (FileProviderUtil.MIME_ZIP == contentResolver.getType(uri)) {
                val zipFile = FileProviderUtil.copyFileToCache(applicationContext, uri)
                if (zipFile?.exists() == true) {
                    val paths = ZipUtils.upZipFile(zipFile, FileUtils.DEFAULT_TEMP_FOLDER)
                    if (paths.size > 0) {
                        paths.forEach {
                            //这里已经是可访问的文件路径了 但是为了统一处理 还是把它转成uri
                            if (!saveFileToNote(it)) {
                                isAllComplete = false
                            } else {
                                File(it).delete()
                            }
                        }
                    }
                }
            } else {
                isAllComplete = false
            }
        }
        val msg = mActionDoneHandler.obtainMessage()
        if (isAllComplete)
            msg.obj = EventImportComplete(0, 1, true)
        else
            msg.obj = EventImportComplete(false, "导入完成，但中间可能出了点小问题")
        mActionDoneHandler.sendMessage(msg)
    }


    private fun saveFileToNote(path:String):Boolean{
        val content = FileUtils.readTextFormFile(path)
        val info  = DataMigrateHelper.getMigrateFileInfo(path)
        return saveNote(content,info)
    }

    private fun saveFileToNote(uri: Uri): Boolean {
        //直接解析文本文件，将其存入数据库
        val content = readTextFromUri(uri)
        val fileName = FileProviderUtil.getFileNameFromUri(applicationContext, uri) ?: return false
        val info = DataMigrateHelper.getMigrateFileInfo(fileName)
        return saveNote(content,info)
    }

    private fun saveNote(content:String,info: Array<String>):Boolean{
        val newPath = FileUtils.createFileWithTime(ConfigManager.fileSavedPath)
        val lastEditTime = if (TextUtils.isEmpty(info[1])) {
            System.currentTimeMillis()
        } else {
            info[1]!!.toLong()
        }
        NoteDataHandler.instance.saveData(info[0], newPath,
                StringUtils.getPreview(content), lastEditTime)
        return FileUtils.writeTextToFile(newPath, content)
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }
}
