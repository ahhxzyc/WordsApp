package com.example.android.wordsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.wordsapp.data.WordsContract.WordsEntry;

/**
 * The ContentProvider of the Words app
 */
public class WordsProvider extends ContentProvider {

    // The matcher used to resolve uri matches
    private UriMatcher mMatcher;

    // Constants for uri matches
    private final int CODE_WORDS = 100;
    private final int CODE_WORD_ITEM = 101;

    // Build up the uri matcher on creation of this Provider. For the reason that Providers are automatically created
    // by AndroidStudio, this process has to be written in the overrode method onCreate().
    @Override
    public boolean onCreate() {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(
                WordsContract.CONTENT_AUTHORITY,
                WordsContract.PATH_WORDS + "/",
                CODE_WORDS
        );
        mMatcher.addURI(
                WordsContract.CONTENT_AUTHORITY,
                WordsContract.PATH_WORDS + "/#",
                CODE_WORD_ITEM
        );
        return true;
    }

    /**
     * **************************** The query part ******************************
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor = null;
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                retCursor = queryWords(
                        uri,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                break;
            case CODE_WORD_ITEM:
                retCursor = queryWords(
                        uri,
                        projection,
                        WordsEntry._ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("bad uri for query(): " + uri);
        }
        // Every time I get a cursor, I set it to watch for any changes in the provider's database
        // The notification will be transferred through the resolver.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor queryWords(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getReadableDatabase();
        return db.query(
                WordsEntry.TABLE_NAME,
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
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                return WordsEntry.CONTENT_TYPE;
            case CODE_WORD_ITEM:
                return WordsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("bad uri for getType(): " + uri);
        }
    }

    /**
     * ********************* The insert part **************************
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                id = insertWords(uri, values);
                break;
            default:
                throw new IllegalArgumentException("bad uri for insert(): " + uri);
        }
        if (id <= 0) {
            throw new UnsupportedOperationException("insert failed for uri: " + uri);
        }
        Uri retUri = WordsEntry.buildWordsUri(id);
        getContext().getContentResolver().notifyChange(retUri, null);
        return WordsEntry.buildWordsUri(id);
    }

    private long insertWords(Uri uri, ContentValues values) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getWritableDatabase();
        return db.insert(WordsEntry.TABLE_NAME, null, values);
    }

    /**
     * ************************ The delete part *******************************
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = 0;
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                rows = deleteWords(uri, selection, selectionArgs);
                break;
            case CODE_WORD_ITEM:
                rows = deleteWords(
                        uri,
                        WordsEntry._ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(WordsEntry.CONTENT_URI, null);
        return rows;
    }

    private int deleteWords(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getWritableDatabase();
        return db.delete(WordsEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * ****************************** The update part ****************************
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = 0;
        switch (mMatcher.match(uri)) {
            case CODE_WORDS:
                rows = updateWords(uri, values, selection, selectionArgs);
                break;
            case CODE_WORD_ITEM:
                rows = updateWords(
                        uri,
                        values,
                        WordsEntry._ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    private int updateWords(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = WordsDbHelper.getInstance(getContext()).getWritableDatabase();
        return db.update(WordsEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
