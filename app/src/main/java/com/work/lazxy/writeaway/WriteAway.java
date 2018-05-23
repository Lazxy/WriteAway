package com.work.lazxy.writeaway;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Lazxy on 2017/4/27.
 */

public class WriteAway extends Application{
    public static Context appContext;

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = this;
    }
}
