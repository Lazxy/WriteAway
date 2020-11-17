package com.work.lazxy.writeaway.event

/**
 * Created by Lazxy on 2018/7/22.
 */
class EventImportComplete {
    var mIsSuccessful: Boolean
    var mErrorMessage: String = ""
    var mCurrentIndex = 0
    var mTaskSum = 0

    constructor(index: Int, taskSum: Int, isSuccessful: Boolean) {
        mIsSuccessful = isSuccessful
        mCurrentIndex = index
        mTaskSum = taskSum
    }

    constructor(isSuccessful: Boolean, errorMessage: String) {
        mIsSuccessful = isSuccessful
        mErrorMessage = errorMessage
    }
}