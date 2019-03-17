package com.work.lazxy.writeaway.utils;


import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import static com.work.lazxy.writeaway.ui.filter.LineBreakInputFilter.INDENT;

/**
 * Created by Lazxy on 2017/5/26.
 */

public class FileUtils {
    public static final String DEFAULT_COMPRESS_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/WriteAway/output/";
    public static final String DEFAULT_TEMP_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/WriteAway/temp/";
    public static final String TYPE_TEXT = ".txt";
    public static final String TYPE_ZIP = ".zip";
    public static String createFileWithTime(String rootPath) {
        return rootPath + System.currentTimeMillis()+TYPE_TEXT;
    }

    public static File createDefaultCompressFile(){
        File file = new File(DEFAULT_COMPRESS_FOLDER+
                CalendarUtils.getFormatTime(Calendar.getInstance().getTime())+TYPE_ZIP);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public static String readTextFormFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
//            file.createNewFile();
            return INDENT;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            return buffer.delete(buffer.length()-1,buffer.length()).toString();//这里要除去最后一个换行符
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static boolean writeTextToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void deleteFolderWithFiles(File file){
        if(file.exists()){
            if(file.isDirectory()){
                for(File childFilePath: file.listFiles()){
                    deleteFolderWithFiles(childFilePath);
                }
            }
            file.delete();
        }
    }
}
