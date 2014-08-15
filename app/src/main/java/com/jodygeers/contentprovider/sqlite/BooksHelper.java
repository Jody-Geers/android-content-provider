package com.jodygeers.contentprovider.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BooksHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Books";
    public static final String DATABASE_TABLE = "titles";
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " ("
            + "_id integer primary key autoincrement, "
            + " title text not null,"
            + " isbn text not null"
            + ");"
            ;

    private static final String DATABASE_DELETE = "DROP TABLE IF EXISTS titles";

    public BooksHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /**
     * Create DB
     * @param db
     */
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( DATABASE_CREATE );
    }

    /**
     * Destory previous version of DB and create new.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {

        System.out.println( "Content provider database Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data" );

        db.execSQL( DATABASE_DELETE );
        onCreate(db);

    }

}