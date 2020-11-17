package com.work.lazxy.writeaway.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.SparseArray
import com.work.lazxy.writeaway.WriteAway
import com.work.lazxy.writeaway.entity.PlanningEntity

/**
 * Created by Lazxy on 2017/4/26.
 */
class PlanningDataHandler private constructor() {
    private val mDatabase: SQLiteDatabase
    private val COLUMN_PRIORITY = "priority"
    private val COLUMN_GOAL = "goal"

    /**
     * 获取数据库中所有数据，以键值对的方式返回，实际不需要这么麻烦，但这样做可以避免以后改动可能出现的乱序问题
     * @return planning表中的所有数据
     */
    fun getPlannings(): SparseArray<String> {
        val plannings = SparseArray<String>()
        val cursor: Cursor = mDatabase.query(DataOpenHelper.PLANNING_TABLE_NAME, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val priority = cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY))
                val goal = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL))
                plannings.append(priority, goal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return plannings
    }

    /**
     * 以一种粗暴的方式完成数据的更新，之后可能考虑细分出删除、新增和调换数据位置的各个方法
     * @param plannings 需要被存入数据库的所有数据
     */
    fun savePlannings(plannings: List<PlanningEntity>) {
        mDatabase.beginTransaction()
        try {
            mDatabase.delete(DataOpenHelper.PLANNING_TABLE_NAME, null, null) //先删除所有数据
            for (item in plannings) {
                val value = ContentValues()
                value.put(COLUMN_PRIORITY, item.priority)
                value.put(COLUMN_GOAL, item.goal)
                mDatabase.insert(DataOpenHelper.PLANNING_TABLE_NAME, null, value)
            }
            mDatabase.setTransactionSuccessful()
        } finally {
            mDatabase.endTransaction()
        }
    }

    fun addPlanning(planning: PlanningEntity) {
        mDatabase.beginTransaction()
        try {
            val value = ContentValues()
            value.put(COLUMN_PRIORITY, planning.priority)
            value.put(COLUMN_GOAL, planning.goal)
            mDatabase.insert(DataOpenHelper.PLANNING_TABLE_NAME, null, value)
            mDatabase.setTransactionSuccessful()
        } finally {
            mDatabase.endTransaction()
        }
    }

    val planningsCount: Int
        get() {
            val countMeasure = mDatabase.rawQuery("select count(*) from " + DataOpenHelper.PLANNING_TABLE_NAME, null)
            countMeasure.moveToFirst()
            val count = countMeasure.getLong(0)
            countMeasure.close()
            return count.toInt()
        }

    companion object {
        val instance: PlanningDataHandler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PlanningDataHandler()
        }
    }

    init {
        val helper = DataOpenHelper(WriteAway.appContext, null, DataOpenHelper.DATABASE_VERSION)
        mDatabase = helper.writableDatabase
    }
}