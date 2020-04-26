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

    private String mTableName;
    private String mLastSegment;
    private long mId;

    @Override
    public boolean onCreate() {
        return true; // meaning the provider is successfully loaded
    }

    private void parseUri(Uri uri) {
        mTableName = uri.getPath().substring(1);
        if (mTableName == null) {
            throw new UnsupportedOperationException("bad uri: " + uri);
        }
        mLastSegment = uri.getLastPathSegment();
        if (mLastSegment != null && !mLastSegment.equals(mTableName)) {
            mId = Long.parseLong(mLastSegment);
        } else {
            mLastSegment = null;
        }
    }

    /**
     * **************************** The query part ******************************
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor = null;
        parseUri(uri);
        if (mLastSegment == null) {
            retCursor = queryWords(
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );
        } else {
            retCursor = queryWords(
                    uri,
                    projection,
                    WordsEntry._ID + "=?",
                    new String[]{String.valueOf(ContentUris.parseId(uri))},
                    sortOrder
            );
        }
        // Every time I get a cursor, I set it to watch for any changes in the provider's database
        // The notification will be transferred through the resolver.
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    private Cursor queryWords(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext(), mTableName).getReadableDatabase();
        return db.query(
                mTableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * ***************** Decide what type of uri this is ***********************
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        parseUri(uri);
        if (mLastSegment == null) {
            return WordsEntry.buildContentType(mTableName);
        } else {
            return WordsEntry.buildContentItemType(mTableName);
        }
    }

    /**
     * ********************* The insert part **************************
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;
        parseUri(uri);
        if (mLastSegment == null) {
            id = insertWords(uri, values);
        } else {
            throw new IllegalArgumentException("bad uri for insert(): " + uri);
        }
        if (id <= 0) {
            throw new UnsupportedOperationException("insert failed for uri: " + uri);
        }
        Uri retUri = WordsEntry.buildWordsUri(mTableName, id);
        getContext().getContentResolver().notifyChange(retUri, null);
        return retUri;
    }

    private long insertWords(Uri uri, ContentValues values) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext(), mTableName).getWritableDatabase();
        return db.insert(mTableName, null, values);
    }

    /**
     * ************************ The delete part *******************************
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = 0;
        parseUri(uri);
        if (mLastSegment == null) {
            rows = deleteWords(uri, selection, selectionArgs);
        } else {
            rows = deleteWords(
                    uri,
                    WordsEntry._ID + "=?",
                    new String[] {String.valueOf(ContentUris.parseId(uri))}
            );
        }
        getContext().getContentResolver().notifyChange(
                WordsEntry.buildContentUri(mTableName),
                null
        );
        return rows;
    }

    private int deleteWords(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext(), mTableName).getWritableDatabase();
        return db.delete(mTableName, selection, selectionArgs);
    }

    /**
     * ****************************** The update part ****************************
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = 0;
        parseUri(uri);
        if (mLastSegment == null) {
            rows = updateWords(uri, values, selection, selectionArgs);
        } else {
            rows = updateWords(
                    uri,
                    values,
                    WordsEntry._ID + "=?",
                    new String[] {String.valueOf(ContentUris.parseId(uri))}
            );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    private int updateWords(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext(), mTableName).getWritableDatabase();
        return db.update(mTableName, values, selection, selectionArgs);
    }
}
