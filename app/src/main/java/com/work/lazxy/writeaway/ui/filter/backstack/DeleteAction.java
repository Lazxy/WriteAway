package com.work.lazxy.writeaway.ui.filter.backstack;

/**
 * Created by Lazxy on 2017/5/23.
 */

public class DeleteAction implements Action {
    private String mOri;   // 被删除的文本

    private int    mPoint; // 被删除时文本的起始位置

    public DeleteAction(String ori, int point) {
        mOri = ori;
        mPoint = point;
    }

    @Override
    public String revoke(String src) {
        StringBuilder buf = new StringBuilder(src);
        buf.insert(mPoint, mOri);
        return buf.toString();
    }

    @Override
    public int getOriginPosition() {
        return mPoint + mOri.length();
    }
}
