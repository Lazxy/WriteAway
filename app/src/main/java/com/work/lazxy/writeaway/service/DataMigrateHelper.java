package com.work.lazxy.writeaway.service;

import android.text.TextUtils;

import com.work.lazxy.writeaway.WriteAway;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lazxy on 2019/3/3.
 */
public class DataMigrateHelper {
    private static final String MIGRATE_CACHE_FOLDER = WriteAway.appContext.getFilesDir().getPath()+"/MigrateCache/";

    private static final String DEFAULT_NOTE_TITLE = "未命名";

    private static final String EDIT_TIME_SEPARATOR = "#￥@#";

    public static List<File> renameFileWithCreateTime(List<NoteEntity> noteList){
        List<File> migrateFiles = new ArrayList<>();
        for(NoteEntity note: noteList){
            File noteFile = new File(note.getFilePath());
            if(noteFile.exists()){
                //这里的命名理论上是不会重复的，除非上次修改时间属性在数据库中被批量插入了同样的值
                File migrateFile = new File(getMigrateFilePath(note));
                try {
                    FileUtils.writeTextToFile(migrateFile.getPath(),FileUtils.readTextFormFile(noteFile.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                migrateFiles.add(migrateFile);
            }
        }
        return migrateFiles;
    }

    public static void clearMigrateCache(){
        File cacheFolder = new File(MIGRATE_CACHE_FOLDER);
        if(cacheFolder.exists()){
            FileUtils.deleteFolderWithFiles(cacheFolder);
        }
    }

    /**
     * 获得迁移文件的标题和上次修改时间（如果有的话）
     * @return 一个存放着标题与修改时间的数组，第一个元素为标题，第二个元素为修改时间
     */
    public static String[] getMigrateFileInfo(String filePath){
        if(filePath.endsWith(FileUtils.TYPE_TEXT)){
            //去除.txt后缀
            filePath = filePath.substring(0,filePath.length() - FileUtils.TYPE_TEXT.length());
        }
        String[] info = new String[2];
        if(!TextUtils.isEmpty(filePath)){
            String fileName = "";
            String[] fileNameParts = filePath.split("/");
            if(fileNameParts.length > 0){
                fileName = fileNameParts[fileNameParts.length - 1];
            }
            String[] titleAndTime = fileName.split(EDIT_TIME_SEPARATOR);
            if(titleAndTime.length == 2){
                info = titleAndTime;
            }else if(titleAndTime.length > 2){
                //此时证明在title中也存在分割标记
                info[1] = titleAndTime[titleAndTime.length - 1];
                info[0] = fileName.substring(0,info[1].length());
            }else{
                info[0] = fileName;
            }
        }
        return info;
    }

    private static String getMigrateFilePath(NoteEntity note){
        String fileName;
        if(TextUtils.isEmpty(note.getTitle())){
            fileName = DEFAULT_NOTE_TITLE;
        }else{
            fileName= note.getTitle();
        }

        return MIGRATE_CACHE_FOLDER + fileName + EDIT_TIME_SEPARATOR + note.getLastEditTime() + EDIT_TIME_SEPARATOR + FileUtils.TYPE_TEXT;
    }
}
