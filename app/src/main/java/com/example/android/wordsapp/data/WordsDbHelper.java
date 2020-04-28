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

    // A private constructor to prevent unnecessary duplicates
    private WordsDbHelper(Context context) {
        super(context, DB_FILENAME, null, DB_VERSION);
    }

    // A public getter method for the client to use
    public static WordsDbHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new WordsDbHelper(context);
        }
        return mHelper;
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + WordsEntry.TABLE_NAME + "(" +
                        WordsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        WordsEntry.COLUMN_WORD + " TEXT," +
                        WordsEntry.COLUMN_WORDLIST+ " TEXT," +
                        WordsEntry.COLUMN_AUDIO + " TEXT)"
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }
}
