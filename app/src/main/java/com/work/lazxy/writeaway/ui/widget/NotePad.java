package com.work.lazxy.writeaway.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.Gravity;

import com.work.lazxy.writeaway.ui.filter.LineBreakInputFilter;
import com.work.lazxy.writeaway.ui.filter.RevokeInputFilter;
import com.work.lazxy.writeaway.ui.filter.backstack.Action;

import java.util.Stack;


/**
 * Created by lenovo on 2017/2/2.
 */

public class NotePad extends android.support.v7.widget.AppCompatEditText {
    private Stack<Action> mStack;

    public NotePad(Context context) {
        super(context);
        mStack = new Stack<>();
    }

    public NotePad(Context context, AttributeSet attr){
        super(context,attr);
        mStack = new Stack<>();
        setGravity(Gravity.START);
        setSingleLine(false);
        setHorizontallyScrolling(false);//禁止单行滚动
        setBackground(null);
        addListeners();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void addListeners(){
        //添加各种需要的监听或者过滤，这里的过滤顺序跟过滤器数组顺序有关
        setFilters(new InputFilter[]{new LineBreakInputFilter(),
        new RevokeInputFilter(mStack)});
    }

    public void revoke(){
        if(isCanRevoke()) {
            Action backAction = mStack.pop();
            setText(Action.REVOKED_SIGN + backAction.revoke(getText().toString()) + Action.REVOKED_SIGN);
            setSelection(backAction.getOriginPosition());
        }
    }

    public boolean isCanRevoke(){
        return mStack.size()>0;
    }

    public void clearRevokeStack(){
        mStack.clear();
    }
}
