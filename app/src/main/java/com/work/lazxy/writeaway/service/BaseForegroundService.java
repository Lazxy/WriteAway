package com.work.lazxy.writeaway.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

/**
 * Created by Lazxy on 2018/7/22.
 */
public abstract class BaseForegroundService<T> extends Service {
    protected Notification.Builder mBuilder;
    public final String FOREGROUND_CHANNEL_NAME = "前台服务";
    public final String FOREGROUND_CHANNEL_ID = "foregroundChannelID";
    protected Handler mActionDoneHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onActionDone((T) msg.obj);
        }
    };
    public final int REQUEST_TO_APPLICATION = 0x11;

    @Override
    public void onCreate() {
        createNotificationChannel();
    }

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

    protected void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, FOREGROUND_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }

    protected Notification.Builder createChannelBuilder() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this.getApplicationContext(), FOREGROUND_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this.getApplicationContext());
        }
        return builder;
    }
}
