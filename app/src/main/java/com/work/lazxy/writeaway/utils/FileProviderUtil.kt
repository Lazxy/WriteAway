package com.work.lazxy.writeaway.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.content.FileProvider
import com.leon.lfilepickerlibrary.LFilePicker
import com.work.lazxy.writeaway.R
import com.work.lazxy.writeaway.WriteAway
import com.work.lazxy.writeaway.common.Constant
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by Lazxy on 2021/5/15.
 */
object FileProviderUtil {
    const val MIME_TEXT = "text/plain"

    const val MIME_ZIP = "application/zip"
    //    "application/x-zip-compressed"

    const val MIME_IMAGE = "image/*"

    //This will be used only on android P-
    private val DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    private val IMAGE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

    fun startForPickFile(activity: Activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            LFilePicker().withActivity(activity)
                    .withStartPath(FileUtils.DEFAULT_COMPRESS_FOLDER)
                    .withChooseMode(true)
                    .withMultiMode(true)
                    .withFileFilter(arrayOf(FileUtils.TYPE_TEXT, FileUtils.TYPE_ZIP))
                    .withRequestCode(Constant.Common.REQUEST_CODE_IMPORT_NOTE)
                    .withTheme(R.style.FilePickerTheme)
                    .withTitle("选择文本/压缩包")
                    .start()
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
//                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(DOWNLOAD_DIR))
            }

            activity.startActivityForResult(intent, Constant.Common.REQUEST_CODE_IMPORT_NOTE)
        }
    }

    @JvmStatic
    fun moveFileToDownloads(context: Context, originFile: File): Uri? {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Android 10及以上的版本，通过请求向Downloads目录插入数据实现迁移
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, getName(originFile))
                put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(originFile))
                put(MediaStore.MediaColumns.SIZE, getFileSize(originFile))
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            //Android 10以下的版本 直接在Download目录创建对象
            val authority = "${context.packageName}.provider"
            val destinyFile = File(DOWNLOAD_DIR, getName(originFile))
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { targetUri ->
            resolver.openOutputStream(targetUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream = BufferedInputStream(FileInputStream(originFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
            //最后删除源文件
            originFile.delete()
        }
    }

    @JvmStatic
    fun moveFileToImages(context: Context,dir:String?, originFile: File): Uri? {
        val dirPath = if(dir?.isEmpty() == true)WriteAway::class.java.simpleName else dir!!
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Android 10及以上的版本，通过请求向Downloads目录插入数据实现迁移
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, getName(originFile))
                put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(originFile))
                put(MediaStore.MediaColumns.SIZE, getFileSize(originFile))
                //这里如果不预设根目录的相对路径 会出现权限匹配失败的问题
                put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES + "/" + dirPath)
            }
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            //Android 10以下的版本 直接在Download目录创建对象
            val authority = "${context.packageName}.provider"
            val destinyFile = File(IMAGE_DIR, getName(originFile))
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { targetUri ->
            resolver.openOutputStream(targetUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream = BufferedInputStream(FileInputStream(originFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
            //最后删除源文件
            originFile.delete()
        }
    }

    @JvmStatic
    fun copyFileToCache(context: Context, originPath: Uri): File? {
        val ips = context.contentResolver.openInputStream(originPath) ?: return null
        val outputFile = File(context.cacheDir.path + "/ZipCache/${System.currentTimeMillis()}")
        if (outputFile.exists()) {
            outputFile.delete()
            outputFile.createNewFile()
        } else {
            outputFile.parentFile?.mkdirs()
            outputFile.createNewFile()
        }
        val ops = FileOutputStream(outputFile)
        ops.write(ips.readBytes())
        ops.close()
        ips.close()
        return outputFile
    }

    @JvmStatic
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = context.contentResolver.query(
                uri, null, null, null, null, null)

        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return null
    }

    private fun getName(file: File): String {
        return file.name
    }

    private fun getMimeType(file: File): String {
        val path = file.path
        if (path.endsWith("zip")) {
            return MIME_ZIP
        } else if (path.endsWith("txt")) {
            return MIME_TEXT
        } else if(path.endsWith("png") || path.endsWith("jpg") || path.endsWith("jpge") || path.endsWith("bmp")){
            return MIME_IMAGE
        }else{
            return MIME_ZIP
        }
    }

    private fun getFileSize(file: File): String {
        return file.length().toString()
    }
}