package com.work.lazxy.writeaway.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;


import com.work.lazxy.writeaway.WriteAway;
import com.work.lazxy.writeaway.entity.PlanningEntity;

import java.util.List;

/**
 * Created by Lazxy on 2017/4/26.
 */

public class PlanningDataHandler {
    private SQLiteDatabase mDatabase;
    private static PlanningDataHandler mInstance;
    private final String COLUMN_PRIORITY = "priority";
    private final String COLUMN_GOAL = "goal";

    private PlanningDataHandler() {
        DataOpenHelper helper = new DataOpenHelper(WriteAway.appContext, null, DataOpenHelper.DATABASE_VERSION);
        mDatabase = helper.getWritableDatabase();
    }

    public static PlanningDataHandler getInstance() {
        if (mInstance == null) {
            synchronized (PlanningDataHandler.class) {
                if (mInstance == null) {
                    mInstance = new PlanningDataHandler();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取数据库中所有数据，以键值对的方式返回，实际不需要这么麻烦，但这样做可以避免以后改动可能出现的乱序问题
     * @return planning表中的所有数据
     */
    public SparseArray<String> getPlannings(){
        Cursor cursor;
        SparseArray<String> plannings = new SparseArray<>();

        cursor = mDatabase.query(DataOpenHelper.PLANNING_TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                int priority = cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY));
                String goal = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL));
                plannings.append(priority,goal);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return plannings;
    }

    /**
     * 以一种粗暴的方式完成数据的更新，之后可能考虑细分出删除、新增和调换数据位置的各个方法
     * @param plannings 需要被存入数据库的所有数据
     */
    public void savePlannings(List<PlanningEntity> plannings){
        mDatabase.beginTransaction();
        try {
            mDatabase.delete(DataOpenHelper.PLANNING_TABLE_NAME,null,null);//先删除所有数据
            for(PlanningEntity item:plannings) {
                ContentValues value = new ContentValues();
                value.put(COLUMN_PRIORITY,item.getPriority());
                value.put(COLUMN_GOAL, item.getGoal());
                mDatabase.insert(DataOpenHelper.PLANNING_TABLE_NAME, null, value);
            }
            mDatabase.setTransactionSuccessful();
        }finally{
            mDatabase.endTransaction();
        }
    }

    public void addPlanning(PlanningEntity planning){
        mDatabase.beginTransaction();
        try{
            ContentValues value = new ContentValues();
            value.put(COLUMN_PRIORITY,planning.getPriority());
            value.put(COLUMN_GOAL,planning.getGoal());
            mDatabase.insert(DataOpenHelper.PLANNING_TABLE_NAME,null,value);
            mDatabase.setTransactionSuccessful();
        }finally {
            mDatabase.endTransaction();
        }
    }

    public int getPlanningsCount(){
        Cursor countMeasure = mDatabase.rawQuery("select count(*) from "+DataOpenHelper.PLANNING_TABLE_NAME, null);
        countMeasure.moveToFirst();
        long count = countMeasure.getLong(0);
        countMeasure.close();
        return (int)count;
    }
}
