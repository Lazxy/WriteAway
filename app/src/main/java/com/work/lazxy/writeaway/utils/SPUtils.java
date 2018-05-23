package com.work.lazxy.writeaway.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Lazxy on 2017/4/26.
 */

public class SPUtils {
    private static final String NORMAL_CONFIG="normalConfig";

    private static final String TASK_CONFIG = "taskConfig";

    private static final String PLANNING_CONFIG = "planningConfig";

    public static void set(Context context, String configFileName, String key, String value){
        SharedPreferences.Editor editor=context.getSharedPreferences(configFileName, Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String get(Context context,String configFileName, String key, String defaultValue){
        SharedPreferences preferences=context.getSharedPreferences(configFileName, Context.MODE_PRIVATE);
        return preferences.getString(key,defaultValue);
    }
}
