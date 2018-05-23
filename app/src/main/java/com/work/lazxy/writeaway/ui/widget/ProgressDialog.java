package com.work.lazxy.writeaway.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;

import com.work.lazxy.writeaway.R;


/**
 * Created by Lazxy on 2017/5/14.
 */

public class ProgressDialog extends AppCompatDialog {
    public ProgressDialog(Context context) {
        super(context,R.style.TransparentDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        setCancelable(false);
    }
}
