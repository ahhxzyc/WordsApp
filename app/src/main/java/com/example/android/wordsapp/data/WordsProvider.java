package com.example.android.wordsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;

/**
 * The ContentProvider of the Words app
 */
public class WordsProvider extends ContentProvider {

    private UriMatcher mMatcher;
    private final int CODE_WORDS = 100;
    private final int CODE_WORDS_ITEM = 101;

    @Override
    public boolean onCreate() {

        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS, CODE_WORDS);
        mMatcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS + "/#", CODE_WORDS_ITEM);

        return true; // meaning the provider is successfully loaded
    }


    /**
     * **************************** The query part ******************************
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor = null;
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getReadableDatabase();
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                retCursor = db.query(
                        WordsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_WORDS_ITEM:
                retCursor = db.query(
                        WordsEntry.TABLE_NAME,
                        projection,
                        WordsEntry._ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("bad uri for query: " + uri);
        }
        // Every time I get a cursor, I set it to watch for any changes in the provider's database
        // The notification will be transferred through the resolver.
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    /**
     * ***************** Decide what type of uri this is ***********************
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                return WordsEntry.CONTENT_TYPE;
            case CODE_WORDS_ITEM:
                return WordsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("bad uri for getType(): " + uri);
        }
    }

    /**
     * ********************* The insert part **************************
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getWritableDatabase();
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                id = db.insert(WordsEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new UnsupportedOperationException("bad uri for insert: " + uri);
        }
        if (id <= 0) {
            throw new UnsupportedOperationException("insert failed for uri: " + uri);
        }
        Uri retUri = WordsEntry.buildWordsUri(id);
        getContext().getContentResolver().notifyChange(retUri, null);
        return retUri;
    }

    /**
     * ************************ The delete part *******************************
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = 0;
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getWritableDatabase();
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                rows = db.delete(
                        WordsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_WORDS_ITEM:
                rows = db.delete(
                        WordsEntry.TABLE_NAME,
                        WordsEntry._ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException("bad uri for delete: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    /**
     * ****************************** The update part ****************************
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = 0;
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getWritableDatabase();
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                rows = db.update(
                        WordsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_WORDS_ITEM:
                rows = db.update(
                        WordsEntry.TABLE_NAME,
                        values,
                        WordsEntry._ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException("bad uri for update: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
