package com.work.lazxy.writeaway.entity;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/2/4.
 */

public class NoteEntity implements Serializable{
    private String mFilePath;
    private String mTitle;
    private String mPreview;
    private long mLastEditTime;

    //这里的参数赋值顺序需要改一下，应用出现了标题、内容和预览错位的情况，待解决，另外Revoke也出现了问题
    public NoteEntity(String title,String preview, String path,long mLastEditTime){
        this.mTitle = title;
        this.mPreview = preview;
        this.mFilePath = path;
        this.mLastEditTime = mLastEditTime;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setFilePath(String address){
        mFilePath = address;
    }

    public String getFilePath(){
        return mFilePath;
    }

    public void setPreview(String preview){
        this.mPreview=preview;
    }

    public String getPreview() {
        return mPreview;
    }

    public void setLastEditTime(long time){
        mLastEditTime = time;
    }

    public long getLastEditTime(){
        return mLastEditTime;
    }
}
