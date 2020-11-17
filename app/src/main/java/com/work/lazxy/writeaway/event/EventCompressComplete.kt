package com.work.lazxy.writeaway.event

/**
 * Created by Lazxy on 2017/5/30.
 */
class EventCompressComplete {
    @JvmField
    var mIsSuccessful: Boolean

    @JvmField
    var mErrorMessage: String? = null

    @JvmField
    var mCurrentIndex = 0

    @JvmField
    var mTaskSum = 0

    constructor(index: Int, taskSum: Int, isSuccessful: Boolean) {
        mIsSuccessful = isSuccessful
        mCurrentIndex = index
        mTaskSum = taskSum
    }

    constructor(isSuccessful: Boolean, errorMessage: String?) {
        mIsSuccessful = isSuccessful
        mErrorMessage = errorMessage
    }
}