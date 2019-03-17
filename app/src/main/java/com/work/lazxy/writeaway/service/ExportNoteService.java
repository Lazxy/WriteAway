package com.work.lazxy.writeaway.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.db.NoteDataHandler;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.event.EventCompressComplete;
import com.work.lazxy.writeaway.ui.activity.MainActivity;
import com.work.lazxy.writeaway.utils.FileUtils;
import com.work.lazxy.writeaway.utils.ZipUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Lazxy on 2017/5/30.
 */

public class ExportNoteService extends BaseForegroundService<EventCompressComplete> {
    private final int EXPORT_NOTIFICATION_ID = 0x10;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mBuilder = createChannelBuilder();
        mBuilder.setContentTitle("正在准备文件")
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setProgress(100, 0, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(EXPORT_NOTIFICATION_ID, mBuilder.build());
        /*这里开始进行压缩文件的异步操作*/
        new Thread(mRunnable).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    protected void onActionDone(EventCompressComplete event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompressComplete(EventCompressComplete event) {
        if (event.mIsSuccessful) {
            int index = event.mCurrentIndex + 1;
            if (index == 1) {
                mBuilder.setContentTitle("导出中");
            }
            mBuilder.setProgress(100, index * 100 / event.mTaskSum, false);
            mBuilder.setContentText(index + " / " + event.mTaskSum);
            startForeground(EXPORT_NOTIFICATION_ID, mBuilder.build());

            if (index == event.mTaskSum) {
                stopForeground(true);
                showEndNotification("导出完成");
                stopSelf();
            }
        } else {
            stopForeground(true);
            showEndNotification(event.mErrorMessage);
            stopSelf();
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            List<NoteEntity> notes = NoteDataHandler.getInstance().getAllData();
            if (notes != null) {
                List<File> outputFiles = DataMigrateHelper.renameFileWithCreateTime(notes);
                final int sum = outputFiles.size();
                if (sum == 0) {
                    EventBus.getDefault().post(new EventCompressComplete(false, "文件丢失，导出失败"));
                    return;
                }
                try {
                    ZipUtils.zipFiles(outputFiles, FileUtils.createDefaultCompressFile(), new ZipUtils.CompressListener() {
                        @Override
                        public void onCompressComplete(int progress, File file) {
                            EventBus.getDefault().post(new EventCompressComplete(progress, sum, true));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new EventCompressComplete(false, "文件丢失，导出失败"));
                    return;
                } finally {
                    DataMigrateHelper.clearMigrateCache();
                }
            } else {
                EventBus.getDefault().post(new EventCompressComplete(false, "数据丢失，导出失败"));
            }
        }
    };

    private void showEndNotification(String msg) {
        Notification.Builder builder = createChannelBuilder();
        Notification notification = builder.setContentTitle("StNote")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setContentIntent(PendingIntent.getActivity(this, REQUEST_TO_APPLICATION, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true)
                .build();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(EXPORT_NOTIFICATION_ID, notification);
    }
}
