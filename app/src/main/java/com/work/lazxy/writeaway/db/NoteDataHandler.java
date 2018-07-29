package com.work.lazxy.writeaway.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.work.lazxy.writeaway.WriteAway;
import com.work.lazxy.writeaway.entity.NoteEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/1.
 */

public class NoteDataHandler {
    private SQLiteDatabase mDatabase;
    private static NoteDataHandler mInstance;

    private NoteDataHandler() {
        DataOpenHelper helper = new DataOpenHelper(WriteAway.appContext, null, DataOpenHelper.DATABASE_VERSION);
        mDatabase = helper.getWritableDatabase();
    }

    public static NoteDataHandler getInstance() {
        if (mInstance == null) {
            synchronized (NoteDataHandler.class) {
                if (mInstance == null) {
                    mInstance = new NoteDataHandler();
                }
            }
        }
        return mInstance;
    }

    /**
     * 存储笔记文件的记录进入数据库
     *
     * @param title    日记的标题
     * @param filePath 文件存储路径，在应用私有目录下。
     * @param preview  日记内容预览，用于编辑前的目录显示。
     * @param editTime 日记的最后编辑时间，以格林威治时间表示
     */
    public boolean saveData(String title, String filePath, String preview, long editTime) {
        mDatabase.beginTransaction();
        try {
            //当该条数据已存在时，删除该条数据
            mDatabase.delete(DataOpenHelper.NOTE_TABLE_NAME,"path=?",new String[]{filePath});
            //然后将数据添加到表的末端
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("path", filePath);
            values.put("preview", preview);
            values.put("edit_time", editTime);
            mDatabase.insert(DataOpenHelper.NOTE_TABLE_NAME, null, values);
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
        return true;
    }

    /**
     * 获取全部日记信息。
     */
    public List<NoteEntity> getAllData(){
        Cursor cursor;
        List<NoteEntity> notes = new ArrayList<>();
        cursor = mDatabase.query(DataOpenHelper.NOTE_TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToLast()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String preview = cursor.getString(cursor.getColumnIndex("preview"));
                long editTime = cursor.getLong(cursor.getColumnIndex("edit_time"));
                NoteEntity note = new NoteEntity(title,preview, path, editTime);
                notes.add(note);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return notes;
    }

    /**
     * 从数据库中获取一定数量的数据记录
     * @param limitIndex 数据查找的起始坐标，查找时从该坐标的下一个数据开始查找
     * @param num 查询的总数据量
     * @return 需要的Note记录
     */
    public List<NoteEntity> getData(int limitIndex, int num) {
        Cursor cursor;
        List<NoteEntity> notes = new ArrayList<>();
        //获得数据库的总条数
        int count = getNotesCount();
        if(limitIndex >= count){
            return notes;
        }
        int start = (int)count - limitIndex - num;//反向取数据，根据待取数据量计算起始点

        //如果数据余量满足num的值，则取出num条数据，否则将剩下的数据全部取出
        if(start<0){
            num = num + start;
            start = 0;
        }
        cursor = mDatabase.rawQuery("select * from " + DataOpenHelper.NOTE_TABLE_NAME + " limit " + num + " offset " + start + " ;", null);
        if (cursor.moveToLast()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String preview = cursor.getString(cursor.getColumnIndex("preview"));
                long editTime = cursor.getLong(cursor.getColumnIndex("edit_time"));
                NoteEntity note = new NoteEntity(title,preview, path, editTime);
                notes.add(note);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return notes;
    }

    public boolean deleteData(String path) {
        int result = 0;
        mDatabase.beginTransaction();
        try {
            result = mDatabase.delete(DataOpenHelper.NOTE_TABLE_NAME, "path=?", new String[]{path});
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
        return result != 0;
    }

    public int getNotesCount(){
        Cursor countMeasure = mDatabase.rawQuery("select count(*) from "+DataOpenHelper.NOTE_TABLE_NAME, null);
        countMeasure.moveToFirst();
        long count = countMeasure.getLong(0);
        countMeasure.close();
        return (int)count;
    }
}
