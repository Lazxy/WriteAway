package com.work.lazxy.writeaway.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.work.lazxy.writeaway.R;

import java.lang.reflect.Method;

/**
 * Created by Lazxy on 2017/2/8.
 */

public class UIUtils {
//    public static ProgressDialog showTransparentProgressDialog(Context context){
//        ProgressDialog dialog=new ProgressDialog(context);
//        Window window=dialog.getWindow();
//        WindowManager.LayoutParams params=window.getAttributes();
//        params.alpha=0.5f;
//        params.dimAmount=0.7f;
//        window.setAttributes(params);
//        dialog.show();
//        return dialog;
//    }
    public static void showSimpleAlertDialog(Context context, String title, String message, String positiveSelection,
                                             String negativeSelection, DialogInterface.OnClickListener positiveListener,
                                             DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveSelection,positiveListener)
                .setNegativeButton(negativeSelection,negativeListener)
                .create().show();
    }
    public static void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("android.support.v7.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //传入参数
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static int measureTextWidth(String text, int textSize){
        Paint fontPaint=new Paint();
        fontPaint.setTextSize(textSize);
        Rect rect=new Rect();
        fontPaint.getTextBounds(text,0,text.length(),rect);
        return rect.width();
    }
    /**
     * 设置输入法隐藏
     * @param context
     */
    public static void hideInputMethod(Context context,View view) {
        //根据当前状态自动弹出或隐藏键盘
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager.hideSoftInputFromWindow(view.getWindowToken(),0));
    }

    /**
     * 设置输入法弹出
     * @param context
     */
    public static void showInputMethod(Context context,View view) {
        //根据当前状态自动弹出或隐藏键盘
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(!inputManager.isActive())
        inputManager.showSoftInput(view,0);
    }

//    /**
//     * 设想是这样的：设置由绿、黄、红3套颜色，选取前1/4为绿色系颜色，中间1/2为黄色系颜色，最后的1/4为红色系
//     * 实际任务数不足时绿色向上取整，其他向下取整
//     * @param context 获取资源颜色所需
//     * @param index 数据所处条目位置
//     * @param sum 数据总量
//     * @return 存放一系颜色的数组
//     */
//    public static int[] choseColorByIndex(Context context, int index, int sum){
//        float real = 0.25f*sum;
//        int greenBorder = real > sum/4?(int)real + 1:(int)real;
//        real = 0.5f*sum;
//        int amberBorder = real > sum/2?(int)real - 1+greenBorder:(int)real+greenBorder;
//        if(index<=greenBorder){
//            return new int[]{context.getResources().getColor(R.color.lightGreen)
//                    ,context.getResources().getColor(R.color.normalGreen)
//                    ,context.getResources().getColor(R.color.greenWhite)};
//        }else if(index<=amberBorder){
//            return new int[]{context.getResources().getColor(R.color.lightAmber)
//                    ,context.getResources().getColor(R.color.normalAmber)
//                    ,context.getResources().getColor(R.color.amberWhite)};
//        }else{
//            return new int[]{context.getResources().getColor(R.color.lightRed)
//                    ,context.getResources().getColor(R.color.normalRed)
//                    ,context.getResources().getColor(R.color.redWhite)};
//        }
//    }
}
