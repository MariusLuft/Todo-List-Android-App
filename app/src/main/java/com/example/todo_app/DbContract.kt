package com.example.todo_app

import android.provider.BaseColumns

object DbContract {

    // Table contents are grouped together in an anonymous object.
    object Todos : BaseColumns {
        const val TABLE_NAME = "Todos"
        const val COLUMN_NAME_TITLE = "Title"
        const val COLUMN_NAME_ISCHECKED = "IsChecked"
    }

    object  SqlCommands {
        const val SQL_CREATE_TABLE =
            "CREATE TABLE ${DbContract.Todos.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${DbContract.Todos.COLUMN_NAME_TITLE} TEXT," +
                    "${DbContract.Todos.COLUMN_NAME_ISCHECKED} BIT)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${DbContract.Todos.TABLE_NAME}"
    }

    object Database{
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Todos.db"
    }
}
