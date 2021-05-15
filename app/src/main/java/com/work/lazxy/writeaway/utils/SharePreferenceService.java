package com.work.lazxy.writeaway.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.work.lazxy.writeaway.WriteAway;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Lazxy on 2017/4/26.
 */

public class SharePreferenceService {
    private static Map<String, SharePreferenceService> spMap = new HashMap<>();

    private SharedPreferences spInstance;

    private SharePreferenceService(String configName) {
        spInstance = WriteAway.appContext.getSharedPreferences(configName, Context.MODE_PRIVATE);
    }

    public static SharePreferenceService getInstance(@NonNull String configName) {
        SharePreferenceService instance = spMap.get(configName);
        if (instance == null) {
            instance = new SharePreferenceService(configName);
            spMap.put(configName, instance);
        }
        return instance;
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = spInstance.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key, String defaultValue) {
        return spInstance.getString(key, defaultValue);
    }
}
