package com.work.lazxy.writeaway.ui.filter.backstack;


/**
 * Created by Lazxy on 2017/5/23.
 */

public class ReplaceAction implements Action {
    private String mOrig;//被替换的字符串
    private int mPoint;//替换的起始点
    private int mLength;//替换字符串的长度

    public ReplaceAction(String orig, int point,int length){
        mOrig = orig;
        mPoint = point;
        mLength = length;
    }

    @Override
    public String revoke(String src) {
        StringBuilder buf = new StringBuilder(src);
        buf.replace(mPoint,mPoint+mLength,"");
        buf.insert(mPoint,mOrig);
        return buf.toString();
    }

    @Override
    public int getOriginPosition() {
        return mPoint+mOrig.length();
    }
}
