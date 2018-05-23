package com.work.lazxy.writeaway.ui.filter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Lazxy on 2017/2/13.
 */

public class EditLengthInputFilter implements InputFilter {
    final int maxLen;

    public EditLengthInputFilter(int length) {
        super();
        this.maxLen = length;
    }

    /**
     * 过滤输入的字符串，中文字符算两个字符，英文字符算一个
     * @param src 输入的文字
     * @param start
     * @param end
     * @param dest 当前已显示的文字
     * @param dstart
     * @param dend
     * @return
     */
    @Override
    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
        int dindex = 0;
        int count = 0;
        //先计算原字符串长度，如果超过了预设值，说明多了半个中文，则减去其字符
        while (count <= maxLen && dindex < dest.length()) {
            char c = dest.charAt(dindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }
        if (count > maxLen) {
            return dest.subSequence(0, dindex - 1);
        }
        //再计算刚输入的字符长度，处理方式同上
        int sindex = 0;
        while (count <= maxLen && sindex < src.length()) {
            char c = src.charAt(sindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        if (count > maxLen) {
            sindex--;
        }

        return src.subSequence(0, sindex);
    }
}
