package com.work.lazxy.writeaway.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.db.NoteDataHandler;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.event.EventCompressComplete;
import com.work.lazxy.writeaway.utils.FileUtils;
import com.work.lazxy.writeaway.utils.ZipUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lazxy on 2017/5/30.
 */

public class ExportNoteService extends Service {
    private Notification.Builder mBuilder;
    private final int NOTIFICATION_ID = 0x128;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        mBuilder = new Notification.Builder(this.getApplicationContext());
        mBuilder.setContentTitle("正在准备文件")
                .setSmallIcon(R.mipmap.ic_notification_small)
                .setProgress(100, 0, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, mBuilder.build());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompressComplete(EventCompressComplete event) {
        if(event.mIsSuccessful) {
            int index = event.mCurrentIndex + 1;
            if (index == 1) {
                mBuilder.setContentTitle("导出中");
            }
            mBuilder.setProgress(100, index * 100 / event.mTaskSum, false);
            mBuilder.setContentText(index + " / " + event.mTaskSum);
            startForeground(NOTIFICATION_ID, mBuilder.build());

            if (index == event.mTaskSum) {
                stopForeground(true);
                showEndNotification("导出完成");
                stopSelf();
            }
        }else{
            stopForeground(true);
            showEndNotification(event.mErrorMessage);
            stopSelf();
        }
    }

    private void showEndNotification(String msg) {
        Notification notification = new Notification.Builder(this.getApplicationContext())
                .setContentTitle("StNote")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_notification_small)
                .build();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            List<NoteEntity> notes = NoteDataHandler.getInstance().getAllData();
            if (notes != null) {
                List<File> files = new ArrayList<>();
                Map<String,Integer> nameMap = new HashMap<>();//用于防止重名的图
                Map<String,String> oldNameMap = new HashMap<>();//用来保存原来和现在名字的对应关系，便于后面的恢复命名
                for (NoteEntity note : notes) {
                    File file = new File(note.getFilePath());
                    if (file.exists()) {
                        String newFileName = TextUtils.isEmpty(note.getTitle())?
                                getResources().getString(R.string.no_title):note.getTitle();
                        if(nameMap.containsKey(newFileName)){
                            //若存在标题重复，则重命名一次
                            int no = nameMap.get(newFileName);
                            nameMap.put(newFileName,no+1);//将重名计数+1
                            newFileName = newFileName+"("+no+")"+".txt";
                        }else{
                            nameMap.put(newFileName,1);
                            newFileName = newFileName+".txt";
                        }
                        File newFile = new File(file.getParent()+File.separator+newFileName);
                        if(oldNameMap.get(newFile.getPath()) == null) {
                            //这里是为了防止用户自己将标题写为XXX(1)的形式而导致的仍然重名
                            oldNameMap.put(newFile.getPath(), file.getPath());
                            file.renameTo(newFile);
                        }
                        files.add(newFile);
                    }
                }
                final int sum = files.size();
                if(sum == 0) {
                    EventBus.getDefault().post(new EventCompressComplete(false,"文件丢失，导出失败"));
                    return;
                }
                try {
                    ZipUtils.zipFiles(files, FileUtils.createDefaultCompressFile(), new ZipUtils.CompressListener() {
                        @Override
                        public void onCompressComplete(int progress, File file) {
                            EventBus.getDefault().post(new EventCompressComplete(progress,sum,true));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new EventCompressComplete(false,"文件读取出错，导出失败"));
                    return;
                }finally{
                    for(File file:files){
                        if(oldNameMap.containsKey(file.getPath())){
                            file.renameTo(new File(oldNameMap.get(file.getPath())));
                        }
                    }
                }
            }else{
                EventBus.getDefault().post(new EventCompressComplete(false,"数据丢失，导出失败"));
            }
        }
    };
}
