package com.work.lazxy.writeaway.event;

/**
 * Created by Lazxy on 2018/7/22.
 */
public class EventImportComplete {
    public boolean mIsSuccessful;
    public String mErrorMessage;
    public int mCurrentIndex;
    public int mTaskSum;

    public EventImportComplete(int index, int taskSum, boolean isSuccessful) {
        mIsSuccessful = isSuccessful;
        mCurrentIndex = index;
        mTaskSum = taskSum;
    }

    public EventImportComplete(boolean isSuccessful, String errorMessage) {
        mIsSuccessful = isSuccessful;
        mErrorMessage = errorMessage;
    }
}
