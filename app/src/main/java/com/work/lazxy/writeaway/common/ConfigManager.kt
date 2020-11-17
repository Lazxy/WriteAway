package com.work.lazxy.writeaway.common

import com.work.lazxy.writeaway.WriteAway
import com.work.lazxy.writeaway.utils.SPUtils

/**
 * Created by lenovo on 2016/11/25.
 */
object ConfigManager {
    private const val NORMAL_CONFIG = "normalConfig"
    const val DEFAULT = "null"
    const val FILE_PATH = "filePath"
    var fileSavedPath: String?
        get() = SPUtils.get(WriteAway.appContext, NORMAL_CONFIG, FILE_PATH, WriteAway.appContext.filesDir.path + "/")
        set(filePath) {
            SPUtils.set(WriteAway.appContext, NORMAL_CONFIG, FILE_PATH, filePath)
        }

    class AppInfo {
        val queryType: String
            get() = SPUtils.get(WriteAway.appContext, NORMAL_CONFIG, QUERY_TYPE, QUERY_BY_DAY)

        companion object {
            private const val QUERY_TYPE = "queryType"
            const val QUERY_BY_DAY = "queryByDay"
            const val QUERY_BY_WEEK = "queryByWeek"
            const val QUERY_BY_MONTH = "queryByMonth"
            fun setQueryType(queryType: String?) {
                SPUtils.set(WriteAway.appContext, NORMAL_CONFIG, QUERY_TYPE, queryType)
            }
        }
    }
}