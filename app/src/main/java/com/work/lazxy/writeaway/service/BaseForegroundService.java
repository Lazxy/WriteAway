package com.work.lazxy.writeaway.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

/**
 * Created by Lazxy on 2018/7/22.
 */
public abstract class BaseForegroundService<T> extends Service {
    protected Notification.Builder mBuilder;
    protected Handler mActionDoneHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onActionDone((T) msg.obj);
        }
    };
    public final int REQUEST_TO_APPLICATION = 0x11;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mActionDoneHandler.removeCallbacksAndMessages(null);
    }

    protected abstract void onActionDone(T t);
}
