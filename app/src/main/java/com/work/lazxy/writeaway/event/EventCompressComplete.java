package com.work.lazxy.writeaway.event;

/**
 * Created by Lazxy on 2017/5/30.
 */

public class EventCompressComplete {
    public boolean mIsSuccessful;
    public String mErrorMessage;
    public int mCurrentIndex;
    public int mTaskSum;

    public EventCompressComplete(int index, int taskSum,boolean isSuccessful) {
        mIsSuccessful = isSuccessful;
        mCurrentIndex = index;
        mTaskSum = taskSum;
    }

    public EventCompressComplete(boolean isSuccessful,String errorMessage){
        mIsSuccessful = isSuccessful;
        mErrorMessage = errorMessage;
    }
}
