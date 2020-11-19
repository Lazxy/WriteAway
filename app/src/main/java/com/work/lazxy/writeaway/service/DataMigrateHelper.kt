package com.work.lazxy.writeaway.service

import android.text.TextUtils
import com.work.lazxy.writeaway.WriteAway
import com.work.lazxy.writeaway.entity.NoteEntity
import com.work.lazxy.writeaway.utils.FileUtils
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by Lazxy on 2019/3/3.
 */
object DataMigrateHelper {
    private val MIGRATE_CACHE_FOLDER = WriteAway.appContext.filesDir.path + "/MigrateCache/"
    private const val DEFAULT_NOTE_TITLE = "未命名"
    private const val EDIT_TIME_SEPARATOR = "#￥@#"

    @JvmStatic
    fun renameFileWithCreateTime(noteList: List<NoteEntity>): List<File> {
        val migrateFiles: MutableList<File> = ArrayList()
        for (note in noteList) {
            val noteFile = File(note.filePath)
            if (noteFile.exists()) {
                //这里的命名理论上是不会重复的，除非上次修改时间属性在数据库中被批量插入了同样的值
                val migrateFile = File(getMigrateFilePath(note))
                try {
                    FileUtils.writeTextToFile(migrateFile.path, FileUtils.readTextFormFile(noteFile.path))
                } catch (e: IOException) {
                    e.printStackTrace()
                    continue
                }
                migrateFiles.add(migrateFile)
            }
        }
        return migrateFiles
    }

    @JvmStatic
    fun clearMigrateCache() {
        val cacheFolder = File(MIGRATE_CACHE_FOLDER)
        if (cacheFolder.exists()) {
            FileUtils.deleteFolderWithFiles(cacheFolder)
        }
    }

    /**
     * 获得迁移文件的标题和上次修改时间（如果有的话）
     * @return 一个存放着标题与修改时间的数组，第一个元素为标题，第二个元素为修改时间
     */
    @JvmStatic
    fun getMigrateFileInfo(filePath: String): Array<String?> {
        var filePath = filePath
        if (filePath.endsWith(FileUtils.TYPE_TEXT)) {
            //去除.txt后缀
            filePath = filePath.substring(0, filePath.length - FileUtils.TYPE_TEXT.length)
        }
        val info = arrayOfNulls<String>(2)
        if (!TextUtils.isEmpty(filePath)) {
            var fileName = ""
            val fileNameParts = filePath.split("/".toRegex()).toTypedArray()
            if (fileNameParts.size > 0) {
                fileName = fileNameParts[fileNameParts.size - 1]
            }
            //按照当前的分隔方式 Kotlin的split会返回最后一项为空的数组，需要将其排除
            var emptyCount = 0;
            val titleAndTime: List<String> = fileName.split(EDIT_TIME_SEPARATOR.toRegex())
            if (TextUtils.isEmpty(titleAndTime.last())) {
                emptyCount++;
            }

            if (titleAndTime.size - emptyCount == 2) {
                info[0] = titleAndTime[0]
                info[1] = titleAndTime[1]
            } else if (titleAndTime.size - emptyCount > 2) {
                //此时证明在title中也存在分割标记
                info[1] = titleAndTime[titleAndTime.size - 1 - emptyCount]
                info[0] = fileName.substring(0, info[1]!!.length)
            } else {
                info[0] = fileName
            }
        }
        return info
    }

    private fun getMigrateFilePath(note: NoteEntity): String {
        val fileName: String = if (TextUtils.isEmpty(note.title)) {
            DEFAULT_NOTE_TITLE
        } else {
            note.title
        }
        return MIGRATE_CACHE_FOLDER + fileName + EDIT_TIME_SEPARATOR + note.lastEditTime + EDIT_TIME_SEPARATOR + FileUtils.TYPE_TEXT
    }
}