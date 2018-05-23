package com.work.lazxy.writeaway.ui.filter.backstack;


/**
 * Created by Lazxy on 2017/5/23.
 */

public class AddAction implements Action {
    private int mStart,mEnd; //插入字符的起始和结束位置

    public AddAction(int start, int end){
        mStart = start;
        mEnd = end;
    }

    @Override
    public String revoke(String src) {
        StringBuilder buf = new StringBuilder(src);
        buf.replace(mStart,mEnd,"");//用空字符串替换增加的字符串以进行撤销操作
        return buf.toString();
    }

    @Override
    public int getOriginPosition() {
        return mStart;
    }
}
