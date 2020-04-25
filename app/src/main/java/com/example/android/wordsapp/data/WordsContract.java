package com.example.android.wordsapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class WordsContract {

    // Something that uniquely identifies the ContentProvider
    public static final String CONTENT_AUTHORITY = "com.example.android.wordsapp";
    public static final String PATH_WORDS = "words";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * The class for the "words" table
     */
    public static class WordsEntry implements BaseColumns {
        // Something that uniquely identifies this table
        public static final String TABLE_NAME = "words";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);
        // The columns of this table
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_AUDIO = "audio";
        // MIME types
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_WORDS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_WORDS;

        public static Uri buildWordsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
