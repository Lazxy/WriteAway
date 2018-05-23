package com.work.lazxy.writeaway.ui.filter;

import android.text.InputFilter;
import android.text.Spanned;

import com.work.lazxy.writeaway.ui.filter.backstack.Action;
import com.work.lazxy.writeaway.ui.filter.backstack.AddAction;
import com.work.lazxy.writeaway.ui.filter.backstack.DeleteAction;
import com.work.lazxy.writeaway.ui.filter.backstack.ReplaceAction;

import java.util.Stack;

/**
 * Created by Lazxy on 2017/5/22.
 */

public class RevokeInputFilter implements InputFilter {
    private Stack<Action> mBackStack; //这里的栈中计划存储三种不同的操作符，分别是添加，删除和替换
    private final int MAX_ITEM_IN_STACK = 20;

    public RevokeInputFilter(Stack<Action> stack){
        mBackStack = stack;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String origin = source.toString();
        if(origin.startsWith(Action.REVOKED_SIGN)&&origin.endsWith(Action.REVOKED_SIGN)){
            //如果这时是一个回退操作
            return origin.substring(3,origin.length()-3);
        }else{
            //若不是，则将该操作加入栈中，待恢复时取用
            int sourceLength = end - start;
            int changedLength = dend - dstart;
            if(changedLength == 0 && sourceLength > 0){
                //此时新增输入，可能是从键盘，也可能是直接粘贴
                int addEnd = source.equals("\n")?dstart+5 : dstart+sourceLength;
                mBackStack.push(new AddAction(dstart,addEnd));
            }else if(sourceLength == 0 && changedLength>0 ){
                //此时为删除
                mBackStack.push(new DeleteAction(dest.toString().substring(dstart,dend),dstart));
            }else if(sourceLength > 0 && changedLength > 0){
                //此时为替换粘贴
                mBackStack.push(new ReplaceAction(dest.toString().substring(dstart,dend),dstart,sourceLength));
            }
            if(mBackStack.size()>= MAX_ITEM_IN_STACK)
                compressBackStack();//控制栈的长度
            return source;
        }

    }

    /**
     * 清除栈的数据，使之始终保存在阈值之下
     */
    private void compressBackStack(){
        Stack<Action> tempStack = new Stack<>();
        for(int i = 0;i < MAX_ITEM_IN_STACK/2; i++)
            tempStack.push(mBackStack.pop());
        mBackStack.clear();
        for(int i = 0;i < tempStack.size(); i++)
            mBackStack.push(tempStack.pop());
    }
}
