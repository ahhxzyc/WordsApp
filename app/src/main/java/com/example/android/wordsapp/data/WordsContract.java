package com.example.android.wordsapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class WordsContract {

    // Something that uniquely identifies the ContentProvider
    public static final String CONTENT_AUTHORITY = "com.example.android.wordsapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * The class for the "words" table
     */
    public static class WordsEntry implements BaseColumns {
        // The columns of this table
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_AUDIO = "audio";

        public static Uri buildWordsUri(String tableName, long id) {
            return ContentUris.withAppendedId(buildContentUri(tableName), id);
        }
        public static Uri buildContentUri(String tableName) {
            return Uri.withAppendedPath(BASE_CONTENT_URI, tableName);
        }
        public static String buildContentType(String tableName) {
            return "vnd.android.cursor.dir/" + buildContentUri(tableName) + "/" + tableName;
        }
        public static String buildContentItemType(String tableName) {
            return "vnd.android.cursor.item/" + buildContentUri(tableName) + "/" + tableName;
        }
    }
}
