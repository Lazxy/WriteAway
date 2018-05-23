package com.work.lazxy.writeaway.ui.filter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Lazxy on 2017/5/21.
 * 为换行加上前置空格
 */

public class LineBreakInputFilter implements InputFilter {
    public static final String INDENT = "    ";
    /**
     * 过滤输入，在字符串替换或者插入时被调用
     * @param source 插入或将替换的字符串（如用a替换b,则source是a,如果是删除操作,则source是""）
     * @param start 目标字符串插入后的起始位置
     * @param end 目标字符串插入后的结束位置
     * @param dest 被替换或者插入前的整体字符内容
     * @param dstart 被替换字符串的起始位置
     * @param dend 被替换字符串的结束位置
     * @return 经处理的source结果，""表示不修改原有字符串，null表示不进行任何修改，继续插入或替换操作
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if(source.equals("\n"))
            return source + INDENT;//换行时添加缩进
        else
            return null;
    }
}
