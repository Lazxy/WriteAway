package com.work.lazxy.writeaway.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2017/2/1.
 */

class DataOpenHelper extends SQLiteOpenHelper {
//    private static final String CREATE_APP_TABLE="create table app_record("
//            +"id integer primary key autoincrement,"
//            +"date integer ,"
//            +"package text ,"
//            +"time integer,"
//            +"launch integer)";

    private static final String CREATE_NOTE_TABLE="create table note("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"path text,"
            +"preview text,"
            +"edit_time integer)";

    private static final String CREATE_PLANNING_TABLE = "create table planning("
            +"id integer primary key autoincrement,"
            +"priority integer,"
            +"goal text)";

    public static final String DATABASE_NAME="data.db";
    public static final String NOTE_TABLE_NAME="note";
    public static final String PLANNING_TABLE_NAME = "planning";
    public static int DATABASE_VERSION=1;

    public DataOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, int version){
        this(context,DATABASE_NAME,factory,version);
    }
    public DataOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_APP_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
        db.execSQL(CREATE_PLANNING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
