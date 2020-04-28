package com.example.android.wordsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;

import java.io.File;

public class WordsDbHelper extends SQLiteOpenHelper {

    // A static instance of this class for the public getter
    private static WordsDbHelper mHelper;

    // The database name and version
    private static String DB_FILENAME = "WordsDb.db";
    private static int DB_VERSION = 1;

    private String mTableName;
    private SQLiteDatabase mDb;

    // A private constructor to prevent unnecessary duplicates
    private WordsDbHelper(Context context, String tableName) {
        super(context, DB_FILENAME, null, DB_VERSION);
        mTableName = tableName;
        mDb = getWritableDatabase();
    }

    // A public getter method for the client to use
    public static WordsDbHelper getInstance(Context context, String tableName) {
        if (mHelper == null) {
            mHelper = new WordsDbHelper(context, tableName);
        }
        mHelper.mTableName = tableName;
        return mHelper;
    }

    public void createTable() {
        mDb.execSQL(
                "CREATE TABLE " + mTableName + "(" +
                        WordsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        WordsEntry.COLUMN_WORD + " TEXT," +
                        WordsEntry.COLUMN_DESCRIPTION + " TEXT," +
                        WordsEntry.COLUMN_AUDIO + " TEXT)"
        );
    }

    public void deleteTable(String tableName) {
        mDb.execSQL(
                "DROP TABLE IF EXISTS " + tableName
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }
}
