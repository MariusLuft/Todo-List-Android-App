package com.example.todo_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class StorageManager(context: Context): SQLiteOpenHelper(context, DbContract.Database.DATABASE_NAME, null, DbContract.Database.DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbContract.SqlCommands.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DbContract.SqlCommands.SQL_DROP_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}