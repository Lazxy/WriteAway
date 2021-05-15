package com.work.lazxy.writeaway.common

import com.work.lazxy.writeaway.WriteAway
import com.work.lazxy.writeaway.utils.SharePreferenceService

/**
 * Created by lenovo on 2016/11/25.
 */
object ConfigManager {
    private const val NORMAL_CONFIG = "normalConfig"
    const val DEFAULT = "null"
    const val FILE_PATH = "filePath"
    val DEFAULT_PATH = WriteAway.appContext.filesDir.path + "/"
    var fileSavedPath: String?
        get() = SharePreferenceService.getInstance(NORMAL_CONFIG)
                .get(FILE_PATH, DEFAULT_PATH)
        set(filePath) {
            SharePreferenceService.getInstance(NORMAL_CONFIG).set(FILE_PATH, filePath)
        }
}