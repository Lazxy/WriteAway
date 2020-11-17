package com.work.lazxy.writeaway.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by lenovo on 2017/2/1.
 */
internal class DataOpenHelper(context: Context?, name: String?, factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    constructor(context: Context?, factory: CursorFactory?, version: Int) : this(context, DATABASE_NAME, factory, version) {}

    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(CREATE_APP_TABLE);
        db.execSQL(CREATE_NOTE_TABLE)
        db.execSQL(CREATE_PLANNING_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DATABASE_NAME = "data.db"
        const val NOTE_TABLE_NAME = "note"
        const val PLANNING_TABLE_NAME = "planning"
        const val DATABASE_VERSION = 1
        private const val CREATE_NOTE_TABLE = ("create table note("
                + "id integer primary key autoincrement,"
                + "title text,"
                + "path text,"
                + "preview text,"
                + "edit_time integer)")
        private const val CREATE_PLANNING_TABLE = ("create table planning("
                + "id integer primary key autoincrement,"
                + "priority integer,"
                + "goal text)")
    }
}